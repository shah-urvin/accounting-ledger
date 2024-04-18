package com.cg.account.posting.saga;

import com.cg.account.command.ChangeWalletBalanceCommand;
import com.cg.account.posting.command.ChangePostingCommand;
import com.cg.account.posting.command.ProcessPostingCommand;
import com.cg.account.posting.constant.PostingStatus;
import com.cg.account.posting.event.PostingChangedEvent;
import com.cg.account.posting.event.PostingCreatedEvent;
import com.cg.account.posting.event.PostingProcessedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@Saga
public class PostingSaga {

    @Inject
    private transient CommandGateway commandGateway;

    private static final Logger logger = LoggerFactory.getLogger(PostingSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "postingId")
    public void handle(PostingCreatedEvent postingCreatedEvent) {
        logger.info("PostingCreatedEvent event invoked...");
        // Send ProcessPostingCommand

        // associate Saga with ProcessPosting.
        /*associateWith("processPostingId",
                postingCreatedEvent.getPostingId());*/

        commandGateway.send(ProcessPostingCommand.builder()
                .accountId(postingCreatedEvent.getAccountId())
                .postingId(postingCreatedEvent.getPostingId())
                .postingStatus(postingCreatedEvent.getPostingStatus())
                .fromWalletId(postingCreatedEvent.getFromWalletId())
                .toWalletId(postingCreatedEvent.getToWalletId())
                .txnAmount(postingCreatedEvent.getTxnAmount())
                .build());
    }

    @SagaEventHandler(associationProperty = "postingId")
    public void handle(PostingProcessedEvent postingProcessedEvent) {
        logger.info("PostingProcessedEvent get invoked...");
        /*associateWith("changedPostingId",
                postingProcessedEvent.getProcessedPostingId());*/
        if(postingProcessedEvent.getPostingStatus().equals(PostingStatus.CLEARED)) {
            commandGateway.send(ChangeWalletBalanceCommand.builder()
                    .accountId(postingProcessedEvent.getAccountId())
                    .assetType(postingProcessedEvent.getAssetType())
                    .walletId(postingProcessedEvent.getWalletId())
                    .newWalletBalance(postingProcessedEvent.getNewWalletBalance())
                    .cryptoType(postingProcessedEvent.getCryptoType())
                    .fundType(postingProcessedEvent.getFundType())
                    .stockSymbol(postingProcessedEvent.getStockSymbol())
                    .timestamp(postingProcessedEvent.getTimestamp())
                    .build());
        }

        commandGateway.send(ChangePostingCommand.builder()
            .postingId(postingProcessedEvent.getPostingId())
            .accountId(postingProcessedEvent.getAccountId())
            .postingStatus(postingProcessedEvent.getPostingStatus())
            .build());
    }

    @SagaEventHandler(associationProperty = "postingId")
    public void handle(PostingChangedEvent postingChangedEvent){
        logger.info("PostingChangedEvent get invoked...");

        // End Saga.
        SagaLifecycle.end();
    }
}
