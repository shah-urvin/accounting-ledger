package com.cg.account.posting.saga;

import com.cg.account.exception.AccountNotFoundException;
import com.cg.account.exception.PostingNotFoundException;
import com.cg.account.posting.entity.Posting;
import com.cg.account.posting.event.ChangePostingStatusEvent;
import com.cg.account.posting.repository.PostingRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class ChangePostingSaga {

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private PostingRepository postingRepository;

    private static final Logger logger = LoggerFactory.getLogger(ChangePostingSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "postingId")
    public void handle(ChangePostingStatusEvent changePostingStatusEvent) {
        // Retrieve Posting
        Posting posting = postingRepository.findById(changePostingStatusEvent.getPostingId()).orElseThrow(() -> new PostingNotFoundException(changePostingStatusEvent.getPostingId()));
        
    }
}
