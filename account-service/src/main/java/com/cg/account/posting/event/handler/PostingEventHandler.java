package com.cg.account.posting.event.handler;

import com.cg.account.entity.Account;
import com.cg.account.entity.Wallet;
import com.cg.account.exception.AccountNotFoundException;
import com.cg.account.posting.entity.Posting;
import com.cg.account.posting.event.PostingCreatedEvent;
import com.cg.account.posting.repository.PostingRepository;
import com.cg.account.repository.AccountRepository;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class PostingEventHandler {

    @Autowired
    private PostingRepository postingRepository;

    @Autowired
    private AccountRepository accountRepository;

    public static final Logger logger = LoggerFactory.getLogger(PostingEventHandler.class);

    @Async
    @EventHandler
    public void on(PostingCreatedEvent postingCreatedEvent) {
        logger.info("PostingCreatedEvent invoked from PostingEventHandler...");
        if(postingCreatedEvent != null) {
            logger.info("PostingCreatedEvent get invoked under PostingEventHandler with the postingId:{}",postingCreatedEvent.getPostingId());
            // Retrieve Account Object
            Account account = accountRepository.findById(postingCreatedEvent.getAccountId()).orElseThrow(() -> new AccountNotFoundException(postingCreatedEvent.getAccountId()));

            Posting posting = new Posting();
            posting.setPostingId(postingCreatedEvent.getPostingId());
            posting.setAccount(account);
            posting.setFromWallet(account.getWallets().stream().filter(wallet -> wallet.getWalletId().equals(postingCreatedEvent.getFromWalletId())).findFirst().get());
            posting.setToWallet(account.getWallets().stream().filter(wallet -> wallet.getWalletId().equals(postingCreatedEvent.getToWalletId())).findFirst().get());
            posting.setTxnAmount(postingCreatedEvent.getTxnAmount());
            posting.setPostingTime(postingCreatedEvent.getPostingTime());
            posting.setPostingStatus(postingCreatedEvent.getPostingStatus());
            postingRepository.save(posting);
            logger.info("PostingCreatedEvent posted in DB with the postingId:{}",postingCreatedEvent.getPostingId());
        }
    }
}
