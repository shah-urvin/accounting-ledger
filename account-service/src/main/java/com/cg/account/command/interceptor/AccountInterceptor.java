package com.cg.account.command.interceptor;


import com.cg.account.entity.AccountLookup;
import com.cg.account.repository.AccountLookupRepository;
import com.cg.account.command.ChangeAccountStatusCommand;
import com.cg.account.command.OpenAccountCommand;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

@Component
public class AccountInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {
    private static final Logger logger = LoggerFactory.getLogger(AccountInterceptor.class);
    private final AccountLookupRepository accountLookupRepository;

    public AccountInterceptor(AccountLookupRepository accountLookupRepository) {
        this.accountLookupRepository = accountLookupRepository;
    }

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(List<? extends CommandMessage<?>> messages) {
        logger.info("Intercepted command invoked...");
        return (index, command) -> {

            logger.info("Intercepted command: " + command.getPayloadType());
            if(ChangeAccountStatusCommand.class.equals(command.getPayloadType())) {
                ChangeAccountStatusCommand changeAccountStatusCommand = (ChangeAccountStatusCommand)command.getPayload();
                List<AccountLookup> lstAccountLookup = accountLookupRepository.findByAccountId(changeAccountStatusCommand.getAccountId());
                if(lstAccountLookup.isEmpty()) {
                    throw new IllegalStateException(
                            String.format("Account with accountId %s does not exist",
                                    changeAccountStatusCommand.getAccountId())
                    );
                }
            } else if(OpenAccountCommand.class.equals(command.getPayloadType())) {
                OpenAccountCommand openAccountCommand = (OpenAccountCommand) command.getPayload();
                logger.info("OpenAccountCommand accountId:{}",openAccountCommand.getAccountId());
                List<AccountLookup> lstAccountLookup = accountLookupRepository.findByAccountId(openAccountCommand.getAccountId());
                if(!lstAccountLookup.isEmpty()) {
                    throw new IllegalStateException(
                            String.format("Account with accountId %s already exist", openAccountCommand.getAccountId())
                    );
                }
            }
            return command;
        };
    }
}
