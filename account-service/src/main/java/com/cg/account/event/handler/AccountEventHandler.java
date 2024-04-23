package com.cg.account.event.handler;

import com.cg.account.entity.*;
import com.cg.account.exception.AccountNotFoundException;
import com.cg.account.repository.AccountRepository;
import com.cg.account.constants.AccountStatus;
import com.cg.account.event.AccountOpenedEvent;
import com.cg.account.event.AccountStatusChangedEvent;
import com.cg.account.event.AccountUpdateProcessedEvent;
import com.cg.account.command.model.*;
import org.axonframework.common.StringUtils;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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
        accountOpenedEvent.getWallets().values().stream().forEach(walletModel -> {
            addWalletFromWalletModel(walletModel,account,null);
        });
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
    public void on(AccountUpdateProcessedEvent accountUpdateProcessedEvent) {
        Account account = accountRepository.findById(accountUpdateProcessedEvent.getAccountId()).orElseThrow(() -> new AccountNotFoundException(accountUpdateProcessedEvent.getAccountId()));
        addWalletFromWalletModel(accountUpdateProcessedEvent.getWallets().get(accountUpdateProcessedEvent.getFromWalletId()),account,accountUpdateProcessedEvent.getFromSymbol() );
        addWalletFromWalletModel(accountUpdateProcessedEvent.getWallets().get(accountUpdateProcessedEvent.getToWalletId()),account,accountUpdateProcessedEvent.getToSymbol() );
    }

    private void addWalletFromWalletModel(WalletModel walletModel, Account account, String symbol) {
        switch (walletModel.getAssetType()) {
            case FIAT_USD -> {
                USDWalletModel usdWalletModel = (USDWalletModel) walletModel;
                account.getWallets().add(USDWallet.builder()
                        .account(account)
                        .walletId(usdWalletModel.getWalletId())
                        .balance(usdWalletModel.getBalance())
                        .timestamp(LocalDateTime.now())
                        .build());
                break;
            }
            case FIAT_HKD -> {
                HKDWalletModel hkdWalletModel = (HKDWalletModel) walletModel;
                account.getWallets().add(HKDWallet.builder()
                        .account(account)
                        .walletId(hkdWalletModel.getWalletId())
                        .balance(hkdWalletModel.getBalance())
                        .timestamp(LocalDateTime.now())
                        .build());
                break;
            }
            case CRYPTO -> {
                CryptoWalletModel cryptoWalletModel = (CryptoWalletModel) walletModel;
                cryptoWalletModel.getCryptoData().values().stream().forEach(cryptoData -> {
                    if(StringUtils.emptyOrNull(symbol) || (StringUtils.nonEmptyOrNull(symbol) && cryptoData.getCryptoType().getSymbol().equals(symbol))) {
                        account.getWallets().add(CryptoWallet.builder()
                                .account(account)
                                .walletId(cryptoWalletModel.getWalletId())
                                .balanceQty(cryptoData.getBalance())
                                .cryptoType(cryptoData.getCryptoType())
                                .timestamp(LocalDateTime.now())
                                .build());
                    }
                });
                break;
            }
            case STOCK -> {
                StockWalletModel stockWalletModel = (StockWalletModel) walletModel;
                stockWalletModel.getStockData().values().stream().forEach(stockData -> {
                    if(StringUtils.emptyOrNull(symbol) || (StringUtils.nonEmptyOrNull(symbol) && stockData.getStockSymbol().getSymbol().equals(symbol))) {
                        account.getWallets().add(StockWallet.builder()
                                .account(account)
                                .walletId(stockWalletModel.getWalletId())
                                .balanceQty(stockData.getBalanceQty())
                                .stockSymbol(stockData.getStockSymbol())
                                .timestamp(LocalDateTime.now())
                                .build());
                    }
                });
                break;
            }
            case FUND -> {
                FundWalletModel fundWalletModel = (FundWalletModel)walletModel;
                fundWalletModel.getFundData().values().stream().forEach(fundData -> {
                    if(StringUtils.emptyOrNull(symbol) || (StringUtils.nonEmptyOrNull(symbol) && fundData.getFundType().getSymbol().equals(symbol))) {
                        account.getWallets().add(FundWallet.builder()
                                .account(account)
                                .walletId(fundWalletModel.getWalletId())
                                .balance(fundData.getBalance())
                                .fundName(fundData.getFundType())
                                .timestamp(LocalDateTime.now())
                                .build());
                    }
                });
                break;
            }
        }
    }

    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception e) throws Exception{
        throw e;
    }

}