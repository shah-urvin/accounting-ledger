package com.cg.account.event.handler;

import com.cg.account.constants.*;
import com.cg.account.entity.*;
import com.cg.account.event.AccountOpenedEvent;
import com.cg.account.event.AccountStatusChangedEvent;
import com.cg.account.exception.AccountNotFoundException;
import com.cg.account.exception.InvalidAccountStatusException;
import com.cg.account.repository.AccountRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ProcessingGroup("account-group")
public class AccountEventHandler {

    @Autowired
    private AccountRepository accountRepository;

    final Logger logger = LoggerFactory.getLogger(AccountEventHandler.class);

    @EventHandler
    public void on(AccountOpenedEvent accountOpenedEvent) {
        logger.info("AccountOpenedEvent event of the AccountEventHandler get called...");
        // Create Account
        Account account = new Account();
        account.setAccountId(accountOpenedEvent.getAccountId());
        account.setStatus(AccountStatus.OPEN);

        // Create 5 Wallets (USDWallet,HKDWallet,CryptoWallet,StockWallet
        // Adding USD Wallet
        account.getWallets().add(USDWallet.builder()
                .account(account)
                .walletId(accountOpenedEvent.getWallets().get(AssetType.FIAT_USD))
                .balance(FiatCurrency.USD.getInitialBalance()).build());

        // Adding HKD Wallet
        account.getWallets().add(HKDWallet.builder()
                .account(account)
                .walletId(accountOpenedEvent.getWallets().get(AssetType.FIAT_HKD))
                .balance(FiatCurrency.HKD.getInitialBalance()).build());

        // Adding the Crypto Wallet
        account.getWallets().add(CryptoWallet.builder()
                .account(account)
                .walletId(accountOpenedEvent.getWallets().get(AssetType.CRYPTO))
                .balanceQty(CryptoType.BTC.getInitalBalance())
                .cryptoType(CryptoType.BTC).build());

        // Adding the Stock Wallet
        account.getWallets().add(StockWallet.builder()
                .account(account)
                .walletId(accountOpenedEvent.getWallets().get(AssetType.STOCK))
                .balanceQty(StockSymbol.CAPGEMINI.getInitialBalance())
                .stockSymbol(StockSymbol.CAPGEMINI).build());

        // Adding the Fund Wallet
        account.getWallets().add(FundWallet.builder()
                .account(account)
                .walletId(accountOpenedEvent.getWallets().get(AssetType.FUND))
                .balance(FundType.HSBC_SMALL_CAP.getInitialBalance())
                .fundName(FundType.HSBC_SMALL_CAP).build());

        accountRepository.save(account);
        logger.info("AccountId "+accountOpenedEvent.getAccountId()+ " saved in the Database successfully...");
        // Logic to perform the transaction
    }

    @EventHandler
    public void on(AccountStatusChangedEvent accountStatusChangedEvent) {
        System.out.println("AccountStatusChangedEvent got called from AccountEventHandler....");
        Account account = accountRepository.findById(accountStatusChangedEvent.getAccountId()).orElseThrow(() -> new AccountNotFoundException(accountStatusChangedEvent.getAccountId()));

        switch (accountStatusChangedEvent.getStatus()) {
            case CLOSED -> {
                if (account.getStatus() == AccountStatus.OPEN) {
                    account.setStatus(AccountStatus.CLOSED);
                }
                break;
            }
            case HOLD -> {
                if (account.getStatus() != AccountStatus.HOLD) {
                   account.setStatus(AccountStatus.HOLD);
                }
                break;
            }
            case RELEASE -> {
                if (account.getStatus() == AccountStatus.HOLD) {
                    account.setStatus(AccountStatus.OPEN);
                }
                break;
            }
            default -> throw new InvalidAccountStatusException(accountStatusChangedEvent.getStatus());
        }

        // Save Account entity to DB
        accountRepository.save(account);
    }
}
