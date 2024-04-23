package com.cg.account.posting.aggregate;

import com.cg.account.exception.PostingNotFoundException;
import com.cg.account.posting.command.RevertCreatedPostingCommand;
import com.cg.account.posting.entity.Posting;
import com.cg.account.posting.event.PostingCreatedRevertedEvent;
import com.cg.account.posting.repository.PostingRepository;
import com.cg.account.posting.command.ChangePostingCommand;
import com.cg.account.posting.command.CreatePostingCommand;
import com.cg.account.constants.PostingStatus;
import com.cg.account.posting.event.PostingChangedEvent;
import com.cg.account.posting.event.PostingCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.common.StringUtils;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class PostingAggregate {

    @AggregateIdentifier
    private String postingId;
    private String accountId;
    private String fromWalletId;
    private String fromSymbol;
    private String toWalletId;
    private String toSymbol;
    private BigDecimal txnAmount;
    private PostingStatus postingStatus;
    private LocalDateTime postingTime;

    private static final Logger logger = LoggerFactory.getLogger(PostingAggregate.class);

    private PostingRepository postingRepository;

    public PostingAggregate() {}

    @CommandHandler
    public PostingAggregate(CreatePostingCommand createPostingCommand) {
        logger.info("CreatePostingCommand from PostingAggregate get called...");
        // Create the Posting Event
        logger.info("Going to post the PostingCreatedEvent...");
        apply(PostingCreatedEvent.builder()
        .accountId(createPostingCommand.getAccountId())
        .postingId(UUID.randomUUID().toString())
        .fromWalletId(createPostingCommand.getFromWalletId())
                .fromSymbol(createPostingCommand.getFromSymbol())
        .toWalletId(createPostingCommand.getToWalletId())
                .toSymbol(createPostingCommand.getToSymbol())
        .postingStatus(PostingStatus.PENDING)
        .txnAmount(createPostingCommand.getTxnAmount())
        .postingTime(LocalDateTime.now()).build());
    }

    @EventSourcingHandler
    public void on(PostingCreatedEvent postingCreatedEvent,PostingRepository postingRepository) {
        logger.info("PostingCreatedEvent from the PostingAggregate invoked...");
        this.postingRepository = postingRepository;
        if(postingCreatedEvent != null) {
            // Create a DB entry and initialize
            createPosting(postingCreatedEvent);
            this.accountId = postingCreatedEvent.getAccountId();
            this.postingId = postingCreatedEvent.getPostingId();
            this.fromWalletId = postingCreatedEvent.getFromWalletId();
            this.fromSymbol = postingCreatedEvent.getFromSymbol();
            this.toWalletId = postingCreatedEvent.getToWalletId();
            this.toSymbol = postingCreatedEvent.getToSymbol();
            this.postingStatus = postingCreatedEvent.getPostingStatus();
            this.txnAmount = postingCreatedEvent.getTxnAmount();
            this.postingTime = postingCreatedEvent.getPostingTime();
        }
    }

    @CommandHandler
    public void on(ChangePostingCommand changePostingCommand) {
        logger.info("PostingStatusChangeCommand command invoked...");
        this.postingRepository = postingRepository;
        // Change Posting details

        apply(PostingChangedEvent.builder()
        .accountId(changePostingCommand.getAccountId())
        .postingId(changePostingCommand.getPostingId())
        .postingStatus(changePostingCommand.getPostingStatus()).build());
    }

    @EventSourcingHandler
    public void on(PostingChangedEvent postingChangedEvent, PostingRepository postingRepository) {
        // Save to DB for query
        this.postingRepository = postingRepository;
        Posting posting = postingRepository.findById(postingChangedEvent.getPostingId()).orElseThrow(() -> new PostingNotFoundException(postingChangedEvent.getPostingId()));
        posting.setPostingStatus(postingChangedEvent.getPostingStatus());
        posting.setPostingTime(LocalDateTime.now());
        postingRepository.save(posting);

        // Save current state
        this.postingId = postingChangedEvent.getPostingId();
        this.accountId = postingChangedEvent.getAccountId();
        this.postingTime = posting.getPostingTime();
        this.postingStatus = postingChangedEvent.getPostingStatus();
    }

    @CommandHandler
    public void on(RevertCreatedPostingCommand revertCreatedPostingCommand,PostingRepository postingRepository) {
        this.postingRepository = postingRepository;
        logger.info("RevertCreatedPostingCommand get invoked..");

        Posting posting = postingRepository.findById(revertCreatedPostingCommand.getPostingId()).orElseThrow(() -> new PostingNotFoundException(revertCreatedPostingCommand.getPostingId()));
        posting.setPostingStatus(revertCreatedPostingCommand.getPostingStatus());
        posting.setPostingTime(LocalDateTime.now());
        postingRepository.save(posting);

        apply(PostingCreatedRevertedEvent.builder()
                .accountId(revertCreatedPostingCommand.getAccountId())
                .postingId(revertCreatedPostingCommand.getPostingId())
                .postingStatus(revertCreatedPostingCommand.getPostingStatus())
                .build());
    }

    @EventSourcingHandler
    public void on(PostingCreatedRevertedEvent postingCreatedRevertedEvent) {
        logger.info("PostingCreatedRevertedEvent get invoked...");
        this.postingId=postingCreatedRevertedEvent.getPostingId();
        this.accountId=postingCreatedRevertedEvent.getAccountId();
        this.postingStatus=postingCreatedRevertedEvent.getPostingStatus();
    }

    private void createPosting(PostingCreatedEvent postingCreatedEvent) {
        if(postingCreatedEvent != null &&
                StringUtils.nonEmptyOrNull(postingCreatedEvent.getAccountId()) &&
                StringUtils.nonEmptyOrNull(postingCreatedEvent.getFromWalletId()) &&
                StringUtils.nonEmptyOrNull(postingCreatedEvent.getToWalletId())) {
            // Create Posting with the PENDING status
            // Retrieve Account Object
            logger.info("Retrieve Account details....");
            Posting posting = new Posting();
            posting.setPostingId(postingCreatedEvent.getPostingId());
            posting.setPostingId(postingCreatedEvent.getPostingId());
            posting.setAccountId(postingCreatedEvent.getAccountId());
            posting.setFromWalletId(postingCreatedEvent.getFromWalletId());
            posting.setToWalletId(postingCreatedEvent.getToWalletId());
            posting.setTxnAmount(postingCreatedEvent.getTxnAmount());
            posting.setPostingTime(postingCreatedEvent.getPostingTime());
            posting.setPostingStatus(postingCreatedEvent.getPostingStatus());
            postingRepository.save(posting);
            logger.info("Posting created successfully....");
        }
    }
}