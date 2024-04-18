package com.cg.account.posting.aggregate;

import com.cg.account.posting.command.CreatePostingCommand;
import com.cg.account.posting.command.PostingStatusChangeCommand;
import com.cg.account.posting.constant.PostingStatus;
import com.cg.account.posting.event.ChangePostingStatusEvent;
import com.cg.account.posting.event.PostingCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.common.StringUtils;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

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
    private String toWalletId;
    private BigDecimal txnAmount;
    private PostingStatus postingStatus;
    private LocalDateTime postingTime;

    private static final Logger logger = LoggerFactory.getLogger(PostingAggregate.class);

    public PostingAggregate() {}

    @CommandHandler
    public PostingAggregate(CreatePostingCommand createPostingCommand) {
        // Validate the posting data
        logger.info("CreatePostingCommand from PostingAggregate get called...");
        if(createPostingCommand != null &&
                StringUtils.nonEmptyOrNull(createPostingCommand.getAccountId()) &&
                StringUtils.nonEmptyOrNull(createPostingCommand.getFromWalletId()) &&
                StringUtils.nonEmptyOrNull(createPostingCommand.getToWalletId())) {
            // Create the Posting Event
            logger.info("Going to post the PostingCreatedEvent...");
            apply(PostingCreatedEvent.builder()
            .accountId(createPostingCommand.getAccountId())
            .postingId(UUID.randomUUID().toString())
            .fromWalletId(createPostingCommand.getFromWalletId())
            .toWalletId(createPostingCommand.getToWalletId())
            .postingStatus(PostingStatus.PENDING)
            .txnAmount(createPostingCommand.getTxnAmount())
            .postingTime(LocalDateTime.now()).build());
        }
    }

    @Async
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
    public void on(PostingStatusChangeCommand postingStatusChangeCommand) {
        logger.info("PostingStatusChangeCommand command invoked...");
        apply(ChangePostingStatusEvent.builder()
        .accountId(postingStatusChangeCommand.getAccountId())
        .postingId(postingStatusChangeCommand.getPostingId())
        .postingStatus(postingStatusChangeCommand.getPostingStatus()).build());
    }

}
