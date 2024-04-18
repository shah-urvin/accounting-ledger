package com.cg.account.command.service;

import com.cg.account.command.ChangeAccountStatusCommand;
import com.cg.account.dto.AccountDTO;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AccountCommandService {

    private Environment environment;
    private CommandGateway commandGateway;

    public AccountCommandService(Environment environment, CommandGateway commandGateway) {
        this.environment = environment;
        this.commandGateway =commandGateway;
    }

    public String accountStatusChange(AccountDTO accountDTO) {
        String returnValue;
        try {
            returnValue = commandGateway.sendAndWait(ChangeAccountStatusCommand.builder().accountId(accountDTO.getAccountId())
                    .status(accountDTO.getAccountStatus())
                    .build());
        }catch (Exception e){
            returnValue = e.getLocalizedMessage();
        }
        return returnValue;
        //accountDTO.setMessage("Account Status updated successfully.");

    }
}
