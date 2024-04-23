package com.cg.account.posting.command.service;

import com.cg.account.dto.PostingDTO;
import com.cg.account.posting.command.CreatePostingCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostingCommandService {

    @Autowired
    private CommandGateway commandGateway;

    private static final Logger logger = LoggerFactory.getLogger(PostingCommandService.class);

    public void createPosting(List<PostingDTO> lstPosting) {
        if(lstPosting != null) {
            logger.info("createPosting service invoked...");
            lstPosting.stream().parallel().forEach(postingDTO -> {
                commandGateway.send(CreatePostingCommand.builder()
                .accountId(postingDTO.getAccountId())
                .fromWalletId(postingDTO.getFromWalletId())
                .fromSymbol(postingDTO.getFromSymbol())
                .toWalletId(postingDTO.getToWalletId())
                .toSymbol(postingDTO.getToSymbol())
                .txnAmount(postingDTO.getTxnAmount()).build());
            });
            logger.info("createPosting service completed...");
        }
    }

    public void updatePostingStatus(List<PostingDTO> lstPosting) {
        if(lstPosting != null) {
            logger.info("updatePostingStatus service invoked...");

            logger.info("updatePostingStatus service completed...");

        }
    }
}
