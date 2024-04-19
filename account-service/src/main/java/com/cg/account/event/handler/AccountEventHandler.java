package com.cg.account.event.handler;

import com.cg.account.command.model.*;
import com.cg.account.constants.AccountStatus;
import com.cg.account.constants.AssetType;
import com.cg.account.entity.*;
import com.cg.account.event.AccountOpenedEvent;
import com.cg.account.event.AccountStatusChangedEvent;
import com.cg.account.exception.AccountNotFoundException;
import com.cg.account.repository.AccountRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("account-group")
public class AccountEventHandler {

    @Autowired
    private AccountRepository accountRepository;

    final Logger logger = LoggerFactory.getLogger(AccountEventHandler.class);

    @EventHandler
    public void on(AccountOpenedEvent accountOpenedEvent) {
        // Create Account
        Account account = new Account();
        account.setAccountId(accountOpenedEvent.getAccountId());
        account.setStatus(AccountStatus.OPEN);

        // Create 5 Wallets (USDWallet,HKDWallet,CryptoWallet,StockWallet
        // Adding USD Wallet
        USDWalletModel usdWalletModel = (USDWalletModel) accountOpenedEvent.getWallets().get(AssetType.FIAT_USD);
        account.getWallets().add(USDWallet.builder()
                .account(account)
                .walletId(usdWalletModel.getWalletId())
                .balance(usdWalletModel.getBalance()).build());

        // Adding HKD Wallet
        HKDWalletModel hkdWalletModel = (HKDWalletModel) accountOpenedEvent.getWallets().get(AssetType.FIAT_HKD);
        account.getWallets().add(HKDWallet.builder()
                .account(account)
                .walletId(hkdWalletModel.getWalletId())
                .balance(hkdWalletModel.getBalance()).build());

        // Adding the Crypto Wallet
        CryptoWalletModel cryptoWalletModel = (CryptoWalletModel) accountOpenedEvent.getWallets().get(AssetType.CRYPTO);
        account.getWallets().add(CryptoWallet.builder()
                .account(account)
                .walletId(cryptoWalletModel.getWalletId())
                .balanceQty(cryptoWalletModel.getBalanceQty())
                .cryptoType(cryptoWalletModel.getCryptoType()).build());

        // Adding the Stock Wallet
        StockWalletModel stockWalletModel = (StockWalletModel) accountOpenedEvent.getWallets().get(AssetType.STOCK);
        account.getWallets().add(StockWallet.builder()
                .account(account)
                .walletId(stockWalletModel.getWalletId())
                .balanceQty(stockWalletModel.getBalanceQty())
                .stockSymbol(stockWalletModel.getStockSymbol()).build());

        // Adding the Fund Wallet
        FundWalletModel fundWalletModel = (FundWalletModel) accountOpenedEvent.getWallets().get(AssetType.FUND);
        account.getWallets().add(FundWallet.builder()
                .account(account)
                .walletId(fundWalletModel.getWalletId())
                .balance(fundWalletModel.getBalance())
                .fundName(fundWalletModel.getFundName()).build());

        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountStatusChangedEvent accountStatusChangedEvent) {
        Account account = accountRepository.findById(accountStatusChangedEvent.getAccountId()).orElseThrow(() -> new AccountNotFoundException(accountStatusChangedEvent.getAccountId()));
        account.setStatus(accountStatusChangedEvent.getStatus());
        accountRepository.save(account);
        logger.info("Persisted the account Status");
    }
}