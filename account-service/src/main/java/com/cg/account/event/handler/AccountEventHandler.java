package com.cg.account.event.handler;

import com.cg.account.command.model.*;
import com.cg.account.constants.*;
import com.cg.account.entity.*;
import com.cg.account.event.AccountOpenedEvent;
import com.cg.account.event.AccountStatusChangedEvent;
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

import java.time.LocalDateTime;
import java.util.UUID;

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
                .balance(usdWalletModel.getBalance())
                .timestamp(LocalDateTime.now()).build());

        // Adding HKD Wallet
        HKDWalletModel hkdWalletModel = (HKDWalletModel) accountOpenedEvent.getWallets().get(AssetType.FIAT_HKD);
        account.getWallets().add(HKDWallet.builder()
                .account(account)
                .walletId(hkdWalletModel.getWalletId())
                .balance(hkdWalletModel.getBalance())
                .timestamp(LocalDateTime.now()).build());

        // Adding the Crypto Wallet
        CryptoWalletModel cryptoWalletModel = (CryptoWalletModel) accountOpenedEvent.getWallets().get(AssetType.CRYPTO);
        account.getWallets().add(CryptoWallet.builder()
                .account(account)
                .walletId(cryptoWalletModel.getWalletId())
                .balanceQty(cryptoWalletModel.getBalanceQty())
                .cryptoType(cryptoWalletModel.getCryptoType())
                .timestamp(LocalDateTime.now()).build());

        // Adding the Stock Wallet
        StockWalletModel stockWalletModel = (StockWalletModel) accountOpenedEvent.getWallets().get(AssetType.STOCK);
        account.getWallets().add(StockWallet.builder()
                .account(account)
                .walletId(stockWalletModel.getWalletId())
                .balanceQty(stockWalletModel.getBalanceQty())
                .stockSymbol(stockWalletModel.getStockSymbol())
                .timestamp(LocalDateTime.now()).build());

        // Adding the Fund Wallet
        FundWalletModel fundWalletModel = (FundWalletModel) accountOpenedEvent.getWallets().get(AssetType.FUND);
        account.getWallets().add(FundWallet.builder()
                .account(account)
                .walletId(fundWalletModel.getWalletId())
                .balance(fundWalletModel.getBalance())
                .fundName(fundWalletModel.getFundName())
                .timestamp(LocalDateTime.now()).build());

        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountStatusChangedEvent accountStatusChangedEvent) {
        Account account = accountRepository.findById(accountStatusChangedEvent.getAccountId()).orElseThrow(() -> new AccountNotFoundException(accountStatusChangedEvent.getAccountId()));
        account.setStatus(accountStatusChangedEvent.getStatus());
        accountRepository.save(account);
        logger.info("Persisted the account Status");
    }

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