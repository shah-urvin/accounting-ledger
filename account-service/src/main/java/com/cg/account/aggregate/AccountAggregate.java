package com.cg.account.aggregate;

import com.cg.account.command.ChangeAccountStatusCommand;
import com.cg.account.command.ChangeWalletBalanceCommand;
import com.cg.account.command.OpenAccountCommand;
import com.cg.account.command.model.*;
import com.cg.account.constants.*;
import com.cg.account.entity.*;
import com.cg.account.event.AccountOpenedEvent;
import com.cg.account.event.AccountStatusChangedEvent;
import com.cg.account.event.WalletBalanceChangedEvent;
import com.cg.account.exception.AccountNotFoundException;
import com.cg.account.exception.InvalidAccountStatusException;
import com.cg.account.exception.WalletNotFoundException;
import com.cg.account.repository.AccountRepository;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class AccountAggregate {

    @AggregateIdentifier
    private String accountId;
    private AccountStatus accountStatus;
    private Map<AssetType, WalletModel> wallets = new HashMap<>();

    private AccountRepository accountRepository;

    private static final Logger logger = LoggerFactory.getLogger(AccountAggregate.class);

    public AccountAggregate() {}

    @CommandHandler
    public AccountAggregate(OpenAccountCommand openAccountCommand,AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        // Create Account and wallets.

        openAccount(openAccountCommand);
        apply(AccountOpenedEvent.builder()
        .wallets(this.wallets)
                .accountId(openAccountCommand.getAccountId())
                .status(AccountStatus.OPEN).build());
    }

    @EventSourcingHandler
    public void on(AccountOpenedEvent accountOpenedEvent) {
        logger.info("AccountOpenedEvent get called...."+accountOpenedEvent.getAccountId());
        this.accountId=accountOpenedEvent.getAccountId();
        this.accountStatus=accountOpenedEvent.getStatus();
        this.wallets = accountOpenedEvent.getWallets();
    }

    @CommandHandler
    public void handle(ChangeAccountStatusCommand changeAccountStatusCommand,AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        logger.info("ChangeAccountStatusCommand get called..."+changeAccountStatusCommand.getAccountId());
        changeAccountStatus(changeAccountStatusCommand);
        apply(AccountStatusChangedEvent.builder().accountId(changeAccountStatusCommand.getAccountId()).status(changeAccountStatusCommand.getStatus()).build());
    }

    @EventSourcingHandler
    public void on(AccountStatusChangedEvent accountStatusChangedEvent) {
        logger.info("AccountStatusChangedEvent got called from AccountAggregate...");
        this.accountId=accountStatusChangedEvent.getAccountId();
        this.accountStatus=accountStatusChangedEvent.getStatus();
    }

    @CommandHandler
    public void on(ChangeWalletBalanceCommand changeWalletBalanceCommand,AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        logger.info("Command ChangeWalletBalanceCommand invoked...");
        // Change the newBalance to Wallet
        changeWalletBalance(changeWalletBalanceCommand);

        WalletBalanceChangedEvent walletBalanceChangedEvent = new WalletBalanceChangedEvent();
        BeanUtils.copyProperties(changeWalletBalanceCommand, walletBalanceChangedEvent);
        apply(walletBalanceChangedEvent);
    }

    private void openAccount(OpenAccountCommand openAccountCommand) {
        // Create Account


        // Create 5 Wallets (USDWallet,HKDWallet,CryptoWallet,StockWallet
        // Adding USD Wallet
        this.wallets.put(AssetType.FIAT_USD, USDWalletModel.builder()
                .accountId(openAccountCommand.getAccountId())
                .walletId(UUID.randomUUID().toString())
                .balance(FiatCurrency.USD.getInitialBalance()).build());

        // Adding HKD Wallet
        this.wallets.put(AssetType.FIAT_HKD, HKDWalletModel.builder()
                .accountId(openAccountCommand.getAccountId())
                .walletId(UUID.randomUUID().toString())
                .balance(FiatCurrency.HKD.getInitialBalance()).build());

        // Adding the Crypto Wallet
        this.wallets.put(AssetType.CRYPTO, CryptoWalletModel.builder()
                .accountId(openAccountCommand.getAccountId())
                .walletId(UUID.randomUUID().toString())
                .balanceQty(CryptoType.BTC.getInitalBalance())
                .cryptoType(CryptoType.BTC).build());

        // Adding the Stock Wallet
        this.wallets.put(AssetType.STOCK, StockWalletModel.builder()
                .accountId(openAccountCommand.getAccountId())
                .walletId(UUID.randomUUID().toString())
                .balanceQty(StockSymbol.CAPGEMINI.getInitialBalance())
                .stockSymbol(StockSymbol.CAPGEMINI).build());

        // Adding the Fund Wallet
        this.wallets.put(AssetType.FUND,FundWalletModel.builder()
                .accountId(openAccountCommand.getAccountId())
                .walletId(UUID.randomUUID().toString())
                .balance(FundType.HSBC_SMALL_CAP.getInitialBalance())
                .fundName(FundType.HSBC_SMALL_CAP).build());

        logger.info("AccountId "+openAccountCommand.getAccountId()+ " all Wallets been created...");
    }

    private void changeAccountStatus(ChangeAccountStatusCommand changeAccountStatusCommand) {

        switch (changeAccountStatusCommand.getStatus()) {
            case CLOSED -> {
                this.accountStatus = AccountStatus.CLOSED;
                break;
            }
            case HOLD -> {
                this.accountStatus = AccountStatus.HOLD;
                break;
            }
            case RELEASE -> {
                this.accountStatus= AccountStatus.OPEN;
                break;
            }
            default -> throw new InvalidAccountStatusException(changeAccountStatusCommand.getStatus());
        }
    }

    private void changeWalletBalance(ChangeWalletBalanceCommand changeWalletBalanceCommand) {
        Account account = accountRepository.findById(changeWalletBalanceCommand.getAccountId()).orElseThrow(() -> new AccountNotFoundException(changeWalletBalanceCommand.getAccountId()));
        Wallet accountWallet = account.getWallets().stream()
                .filter(wallet -> wallet.getWalletId().equals(changeWalletBalanceCommand.getWalletId()))
                .findFirst()
                .orElseThrow(() -> new WalletNotFoundException(changeWalletBalanceCommand.getWalletId(), changeWalletBalanceCommand.getAccountId()));
        if(accountWallet != null) {
            switch (changeWalletBalanceCommand.getAssetType()) {
                case FIAT_USD -> {
                    if(accountWallet instanceof USDWallet) {
                        USDWallet usdWallet = (USDWallet) accountWallet;
                        usdWallet.setBalance(changeWalletBalanceCommand.getNewWalletBalance());
                        account.getWallets().add(usdWallet);
                    }
                    break;
                }
                case FIAT_HKD -> {
                    if(accountWallet instanceof HKDWallet) {
                        HKDWallet hkdWallet = (HKDWallet) accountWallet;
                        hkdWallet.setBalance(changeWalletBalanceCommand.getNewWalletBalance());
                        account.getWallets().add(hkdWallet);
                    }
                    break;
                }
                case STOCK -> {
                    if(accountWallet instanceof StockWallet) {
                        StockWallet stockWallet = (StockWallet) accountWallet;
                        stockWallet.setStockSymbol(changeWalletBalanceCommand.getStockSymbol());
                        stockWallet.setBalanceQty(changeWalletBalanceCommand.getNewWalletBalance().longValue());
                        account.getWallets().add(stockWallet);
                    }
                    break;
                }
                case CRYPTO -> {
                    if(accountWallet instanceof CryptoWallet) {
                        CryptoWallet cryptoWallet = (CryptoWallet) accountWallet;
                        cryptoWallet.setCryptoType(changeWalletBalanceCommand.getCryptoType());
                        cryptoWallet.setBalanceQty(changeWalletBalanceCommand.getNewWalletBalance());
                        account.getWallets().add(cryptoWallet);
                    }
                    break;
                }
                case FUND -> {
                    if(accountWallet instanceof FundWallet) {
                        FundWallet fundWallet = (FundWallet) accountWallet;
                        fundWallet.setFundName(changeWalletBalanceCommand.getFundType());
                        fundWallet.setBalance(changeWalletBalanceCommand.getNewWalletBalance());
                        account.getWallets().add(fundWallet);
                    }
                    break;
                }
            }
            accountRepository.save(account);
        }
    }
}