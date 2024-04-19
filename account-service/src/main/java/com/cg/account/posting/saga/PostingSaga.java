package com.cg.account.posting.saga;

import com.cg.account.command.ProcessUpdateAccountCommand;
import com.cg.account.posting.command.ChangePostingCommand;
import com.cg.account.constants.AccountStatus;
import com.cg.account.constants.PostingStatus;
import com.cg.account.event.AccountUpdateProcessedEvent;
import com.cg.account.posting.event.PostingChangedEvent;
import com.cg.account.posting.event.PostingCreatedEvent;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
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

        commandGateway.send(ProcessUpdateAccountCommand.builder()
                        .accountId(postingCreatedEvent.getAccountId())
                        .accountStatus(AccountStatus.OPEN)
                        .postingId(postingCreatedEvent.getPostingId())
                        .fromWalletId(postingCreatedEvent.getFromWalletId())
                        .fromSymbol(postingCreatedEvent.getFromSymbol())
                        .toWalletId(postingCreatedEvent.getToWalletId())
                        .toSymbol(postingCreatedEvent.getToSymbol())
                        .txnAmount(postingCreatedEvent.getTxnAmount()).build()
                , new CommandCallback<ProcessUpdateAccountCommand, Object>() {
                    @Override
                    public void onResult(@Nonnull CommandMessage<? extends ProcessUpdateAccountCommand> commandMessage,
                                         @Nonnull CommandResultMessage<?> commandResultMessage) {
                        if(commandResultMessage.isExceptional()) {
                            // Start a compensating transaction
                        }
                    }
                });
    }

    @SagaEventHandler(associationProperty = "accountId")
    public void handle(AccountUpdateProcessedEvent accountUpdateProcessedEvent) {
        commandGateway.send(ChangePostingCommand.builder()
                .postingId(accountUpdateProcessedEvent.getPostingId())
                .accountId(accountUpdateProcessedEvent.getAccountId())
                .postingStatus(PostingStatus.CLEARED)
                .build());
    }

    @SagaEventHandler(associationProperty = "postingId")
    public void handle(PostingChangedEvent postingChangedEvent){
        logger.info("PostingChangedEvent get invoked...");

        // End Saga.
        SagaLifecycle.end();
        logger.info("Sage End, for Posting:{}",postingChangedEvent.getPostingId());
    }

}
