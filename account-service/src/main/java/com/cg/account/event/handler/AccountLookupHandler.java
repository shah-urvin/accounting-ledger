package com.cg.account.event.handler;

import com.cg.account.entity.AccountLookup;
import com.cg.account.event.AccountOpenedEvent;
import com.cg.account.repository.AccountLookupRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * To setup the Set Base Consistency.
 */

@Component
@ProcessingGroup("account-group")
public class AccountLookupHandler {

    private AccountLookupRepository accountLookupRepository;

    private static final Logger logger = LoggerFactory.getLogger(AccountLookupHandler.class);

    public AccountLookupHandler(AccountLookupRepository accountLookupRepository) {
        this.accountLookupRepository = accountLookupRepository;
    }

    @EventHandler
    public void on(AccountOpenedEvent accountOpenedEvent) {
        logger.info("AccountLookupHandler get called with accountId:{}",accountOpenedEvent.getAccountId());
        accountOpenedEvent.getWallets().values().stream().forEach(wallet -> {
            AccountLookup accountLookup = new AccountLookup();
            accountLookup.setAccountId(accountOpenedEvent.getAccountId());
            accountLookup.setWalletId(wallet.getWalletId());
            accountLookupRepository.save(accountLookup);
        });
        logger.info("accountOpenedEvent completed...");
    }
}
