package com.cg.account.event.handler;

import com.cg.account.entity.*;
import com.cg.account.event.WalletBalanceChangedEvent;
import com.cg.account.exception.AccountNotFoundException;
import com.cg.account.exception.WalletNotFoundException;
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
    public void on(WalletBalanceChangedEvent walletBalanceChangedEvent) {
        logger.info("WalletBalanceUpdatedEvent event invoked....");
        Account account = accountRepository.findById(walletBalanceChangedEvent.getAccountId()).orElseThrow(() -> new AccountNotFoundException(walletBalanceChangedEvent.getAccountId()));
        Wallet accountWallet = account.getWallets().stream()
                .filter(wallet -> wallet.getWalletId().equals(walletBalanceChangedEvent.getWalletId()))
                .findFirst()
                .orElseThrow(() -> new WalletNotFoundException(walletBalanceChangedEvent.getWalletId(), walletBalanceChangedEvent.getAccountId()));
        if(accountWallet != null) {
            switch (walletBalanceChangedEvent.getAssetType()) {
                case FIAT_USD -> {
                    if(accountWallet instanceof USDWallet) {
                        USDWallet usdWallet = (USDWallet) accountWallet;
                        usdWallet.setBalance(walletBalanceChangedEvent.getNewWalletBalance());
                        account.getWallets().add(usdWallet);
                    }
                    break;
                }
                case FIAT_HKD -> {
                    if(accountWallet instanceof HKDWallet) {
                        HKDWallet hkdWallet = (HKDWallet) accountWallet;
                        hkdWallet.setBalance(walletBalanceChangedEvent.getNewWalletBalance());
                        account.getWallets().add(hkdWallet);
                    }
                    break;
                }
                case STOCK -> {
                    if(accountWallet instanceof StockWallet) {
                        StockWallet stockWallet = (StockWallet) accountWallet;
                        stockWallet.setStockSymbol(walletBalanceChangedEvent.getStockSymbol());
                        stockWallet.setBalanceQty(walletBalanceChangedEvent.getNewWalletBalance().longValue());
                        account.getWallets().add(stockWallet);
                    }
                    break;
                }
                case CRYPTO -> {
                    if(accountWallet instanceof CryptoWallet) {
                        CryptoWallet cryptoWallet = (CryptoWallet) accountWallet;
                        cryptoWallet.setCryptoType(walletBalanceChangedEvent.getCryptoType());
                        cryptoWallet.setBalanceQty(walletBalanceChangedEvent.getNewWalletBalance());
                        account.getWallets().add(cryptoWallet);
                    }
                    break;
                }
                case FUND -> {
                    if(accountWallet instanceof FundWallet) {
                        FundWallet fundWallet = (FundWallet) accountWallet;
                        fundWallet.setFundName(walletBalanceChangedEvent.getFundType());
                        fundWallet.setBalance(walletBalanceChangedEvent.getNewWalletBalance());
                        account.getWallets().add(fundWallet);
                    }
                    break;
                }
            }
            accountRepository.save(account);
        }
    }
}