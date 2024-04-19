package com.cg.account.posting.command.interceptor;

import com.cg.account.entity.Account;
import com.cg.account.entity.AccountLookup;
import com.cg.account.exception.AccountNotFoundException;
import com.cg.account.exception.InoperativeAccountException;
import com.cg.account.repository.AccountLookupRepository;
import com.cg.account.repository.AccountRepository;
import com.cg.account.posting.command.CreatePostingCommand;
import com.cg.account.constants.AccountStatus;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;

/**
 * PostingInterceptor interceptor is validating the CreatePostingCommand with below rules.
 * 1. AccountLookup entity must contains the accountId supplied to the CreatePostingCommand
 * 2. fromWalletId, toWalletId must be exist in the WalletLookup entity
 */

@Component
public class PostingInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    @Autowired
    private AccountLookupRepository accountLookupRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends CommandMessage<?>> messages) {
        return (index,command) -> {
            if(CreatePostingCommand.class.equals(command.getPayloadType())) {
                CreatePostingCommand createPostingCommand = (CreatePostingCommand) command.getPayload();
                // Validate the Account status, if it's Open then only allow posting.
                Account account = accountRepository.findById(createPostingCommand.getAccountId()).orElseThrow(() -> new AccountNotFoundException(createPostingCommand.getAccountId()));
                if(account != null && !account.getStatus().equals(AccountStatus.OPEN)) {
                    throw new InoperativeAccountException(createPostingCommand.getAccountId(),account.getStatus());
                }
                // Validate the fromWalletId with accountId
                AccountLookup fromWalletAccountLookup = accountLookupRepository.findByAccountIdAndWalletId(createPostingCommand.getAccountId(),createPostingCommand.getFromWalletId());
                if(fromWalletAccountLookup == null) {
                    throw new IllegalStateException(
                            String.format("Account with accountId %s and walletId %s does not exist",
                                    createPostingCommand.getAccountId(),createPostingCommand.getFromWalletId())
                    );
                }
                // Validate the toWalletId with accountId.
                AccountLookup toWalletAccountLookup = accountLookupRepository.findByAccountIdAndWalletId(createPostingCommand.getAccountId(),createPostingCommand.getToWalletId());
                if(fromWalletAccountLookup == null) {
                    throw new IllegalStateException(
                            String.format("Account with accountId %s and walletId %s does not exist",
                                    createPostingCommand.getAccountId(),createPostingCommand.getToWalletId())
                    );
                }
            }
            return command;
        };
    }
}
