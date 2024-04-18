package com.cg.account.posting.aggregate;

import com.cg.account.entity.Account;
import com.cg.account.exception.AccountNotFoundException;
import com.cg.account.exception.InsufficientBalanceException;
import com.cg.account.exception.PostingNotFoundException;
import com.cg.account.posting.command.ChangePostingCommand;
import com.cg.account.posting.command.CreatePostingCommand;
import com.cg.account.posting.command.ProcessPostingCommand;
import com.cg.account.posting.constant.PostingStatus;
import com.cg.account.posting.dto.WalletChangeDTO;
import com.cg.account.posting.entity.Posting;
import com.cg.account.posting.event.PostingChangedEvent;
import com.cg.account.posting.event.PostingCreatedEvent;
import com.cg.account.posting.event.PostingProcessedEvent;
import com.cg.account.posting.repository.PostingRepository;
import com.cg.account.repository.AccountRepository;
import com.cg.account.transaction.WalletOperations;
import com.cg.account.transaction.factory.WalletOperationsFactory;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.common.StringUtils;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class PostingAggregate {

    @AggregateIdentifier
    private String postingId;
    private String accountId;
    private String fromWalletId;
    private String toWalletId;
    private BigDecimal txnAmount;
    private PostingStatus postingStatus;
    private LocalDateTime postingTime;

    private static final Logger logger = LoggerFactory.getLogger(PostingAggregate.class);

    private PostingRepository postingRepository;
    private AccountRepository accountRepository;

    public PostingAggregate() {}

    @CommandHandler
    public PostingAggregate(CreatePostingCommand createPostingCommand,AccountRepository accountRepository,PostingRepository postingRepository) {
        logger.info("CreatePostingCommand from PostingAggregate get called...");
        this.accountRepository = accountRepository;
        this.postingRepository = postingRepository;

        createPostingCommand.setPostingId(UUID.randomUUID().toString());
        createPostingCommand.setPostingStatus(PostingStatus.PENDING);
        createPostingCommand.setPostingTime(LocalDateTime.now());

        //Create Posting.
        createPosting(createPostingCommand);

        // Create the Posting Event
        logger.info("Going to post the PostingCreatedEvent...");
        apply(PostingCreatedEvent.builder()
        .accountId(createPostingCommand.getAccountId())
        .postingId(createPostingCommand.getPostingId())
        .fromWalletId(createPostingCommand.getFromWalletId())
        .toWalletId(createPostingCommand.getToWalletId())
        .postingStatus(createPostingCommand.getPostingStatus())
        .txnAmount(createPostingCommand.getTxnAmount())
        .postingTime(createPostingCommand.getPostingTime()).build());
    }

    @EventSourcingHandler
    public void on(PostingCreatedEvent postingCreatedEvent) {
        logger.info("PostingCreatedEvent from the PostingAggregate invoked...");
        if(postingCreatedEvent != null) {
            this.accountId = postingCreatedEvent.getAccountId();
            this.postingId = postingCreatedEvent.getPostingId();
            this.fromWalletId = postingCreatedEvent.getFromWalletId();
            this.toWalletId = postingCreatedEvent.getToWalletId();
            this.postingStatus = postingCreatedEvent.getPostingStatus();
            this.txnAmount = postingCreatedEvent.getTxnAmount();
            this.postingTime = postingCreatedEvent.getPostingTime();
        }
    }

    @CommandHandler
    public void on(ProcessPostingCommand processPostingCommand,AccountRepository accountRepository,PostingRepository postingRepository) {
        this.accountRepository= accountRepository;
        this.postingRepository = postingRepository;
        logger.info("ProcessPostingCommand command invoked...");

        if(processPostingCommand.getPostingStatus().equals(PostingStatus.PENDING)) {
            try {
                List<WalletChangeDTO> lstWalletChangeDTO = processPosting(processPostingCommand);
                lstWalletChangeDTO.stream()
                        .forEach(walletChangeDTO -> {
                            apply(PostingProcessedEvent.builder()
                                    .postingId(processPostingCommand.getPostingId())
                                    .accountId(processPostingCommand.getAccountId())
                                    .walletId(walletChangeDTO.getWalletId())
                                    .newWalletBalance(walletChangeDTO.getNewWalletBalance())
                                    .assetType(walletChangeDTO.getAssetType())
                                    .cryptoType(walletChangeDTO.getCryptoType())
                                    .fundType(walletChangeDTO.getFundType())
                                    .stockSymbol(walletChangeDTO.getStockSymbol())
                                    .timestamp(walletChangeDTO.getTimestamp())
                                    .postingStatus(PostingStatus.CLEARED)
                                    .build());
                        });

            } catch(InsufficientBalanceException ibe) {
                apply(PostingProcessedEvent.builder()
                        .postingId(processPostingCommand.getPostingId())
                        .accountId(processPostingCommand.getAccountId())
                        .postingStatus(PostingStatus.FAILED)
                        .build());
            }
        }
    }

    /*@EventSourcingHandler
    protected void on(PostingProcessedEvent postingProcessedEvent) {
        logger.info("PostingProcessedEvent from postingAggregate invoked...");
        this.postingId = postingProcessedEvent.getProcessedPostingId();
        this.accountId = postingProcessedEvent.getAccountId();
        this.postingStatus = postingProcessedEvent.getPostingStatus();
    }*/

    @CommandHandler
    public void on(ChangePostingCommand changePostingCommand, PostingRepository postingRepository) {
        logger.info("PostingStatusChangeCommand command invoked...");
        this.postingRepository = postingRepository;
        // Change Posting details
        Posting posting = postingRepository.findById(changePostingCommand.getPostingId()).orElseThrow(() -> new PostingNotFoundException(changePostingCommand.getPostingId()));
        posting.setPostingStatus(changePostingCommand.getPostingStatus());
        apply(PostingChangedEvent.builder()
        .accountId(changePostingCommand.getAccountId())
        .postingId(changePostingCommand.getPostingId())
        .postingStatus(changePostingCommand.getPostingStatus()).build());
    }

    private void createPosting(CreatePostingCommand createPostingCommand) {
        if(createPostingCommand != null &&
                StringUtils.nonEmptyOrNull(createPostingCommand.getAccountId()) &&
                StringUtils.nonEmptyOrNull(createPostingCommand.getFromWalletId()) &&
                StringUtils.nonEmptyOrNull(createPostingCommand.getToWalletId())) {
            // Create Posting with the PENDING status
            // Retrieve Account Object
            logger.info("Retrieve Account details....");
            Account account = accountRepository.findById(createPostingCommand.getAccountId()).orElseThrow(() -> new AccountNotFoundException(createPostingCommand.getAccountId()));
            Posting posting = new Posting();
            posting.setPostingId(createPostingCommand.getPostingId());
            posting.setPostingId(createPostingCommand.getPostingId());
            posting.setAccount(account);
            posting.setFromWallet(account.getWallets()
                    .stream()
                    .filter(wallet -> wallet.getWalletId().equals(createPostingCommand.getFromWalletId()))
                    .findFirst().get());
            posting.setToWallet(account.getWallets().stream().filter(wallet -> wallet.getWalletId().equals(createPostingCommand.getToWalletId())).findFirst().get());
            posting.setTxnAmount(createPostingCommand.getTxnAmount());
            posting.setPostingTime(createPostingCommand.getPostingTime());
            posting.setPostingStatus(createPostingCommand.getPostingStatus());
            postingRepository.save(posting);
            logger.info("Posting created successfully....");
        }
    }

    private List<WalletChangeDTO> processPosting(ProcessPostingCommand processPostingCommand) {
        logger.info("processPosting get called...");
        Posting posting = postingRepository.findById(processPostingCommand.getPostingId()).orElseThrow(() -> new PostingNotFoundException(processPostingCommand.getPostingId()));
        logger.info("Posting details, postingId:{},fromWalletId:{}",posting.getPostingId(),posting.getFromWallet().getWalletId());
        WalletOperations fromWalletOperations = WalletOperationsFactory.getWalletOperations(posting.getFromWallet().getAssetType());
        WalletOperations toWalletOperations = WalletOperationsFactory.getWalletOperations(posting.getToWallet().getAssetType());

        // Perform debit and credit Operations
        WalletChangeDTO fromWalletChangeDTO = fromWalletOperations.debit(posting.getFromWallet(), posting.getTxnAmount());
        WalletChangeDTO toWalletChangeDTO = toWalletOperations.credit(posting);
        List<WalletChangeDTO> lstWalletChangeDTO = new ArrayList<>();
        lstWalletChangeDTO.add(fromWalletChangeDTO);
        lstWalletChangeDTO.add(toWalletChangeDTO);
        return lstWalletChangeDTO;
    }
}