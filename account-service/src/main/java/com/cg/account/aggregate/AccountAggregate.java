package com.cg.account.aggregate;

import com.cg.account.command.ChangeAccountStatusCommand;
import com.cg.account.command.ChangeWalletBalanceCommand;
import com.cg.account.command.OpenAccountCommand;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class AccountAggregate {

    @AggregateIdentifier
    private String accountId;
    private AccountStatus accountStatus;
    private List<String> wallets;

    private AccountRepository accountRepository;

    private static final Logger logger = LoggerFactory.getLogger(AccountAggregate.class);

    public AccountAggregate() {}

    @CommandHandler
    public AccountAggregate(OpenAccountCommand openAccountCommand,AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        // Create Account and wallets.
        List<String> lstWallets = this.openAccount(openAccountCommand);
        apply(AccountOpenedEvent.builder()
        .wallets(lstWallets)
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

    private List<String> openAccount(OpenAccountCommand openAccountCommand) {
        // Create Account
        Account account = new Account();
        account.setAccountId(openAccountCommand.getAccountId());
        account.setStatus(AccountStatus.OPEN);

        // Create 5 Wallets (USDWallet,HKDWallet,CryptoWallet,StockWallet
        // Adding USD Wallet
        account.getWallets().add(USDWallet.builder()
                .account(account)
                .walletId(UUID.randomUUID().toString())
                .balance(FiatCurrency.USD.getInitialBalance()).build());

        // Adding HKD Wallet
        account.getWallets().add(HKDWallet.builder()
                .account(account)
                .walletId(UUID.randomUUID().toString())
                .balance(FiatCurrency.HKD.getInitialBalance()).build());

        // Adding the Crypto Wallet
        account.getWallets().add(CryptoWallet.builder()
                .account(account)
                .walletId(UUID.randomUUID().toString())
                .balanceQty(CryptoType.BTC.getInitalBalance())
                .cryptoType(CryptoType.BTC).build());

        // Adding the Stock Wallet
        account.getWallets().add(StockWallet.builder()
                .account(account)
                .walletId(UUID.randomUUID().toString())
                .balanceQty(StockSymbol.CAPGEMINI.getInitialBalance())
                .stockSymbol(StockSymbol.CAPGEMINI).build());

        // Adding the Fund Wallet
        account.getWallets().add(FundWallet.builder()
                .account(account)
                .walletId(UUID.randomUUID().toString())
                .balance(FundType.HSBC_SMALL_CAP.getInitialBalance())
                .fundName(FundType.HSBC_SMALL_CAP).build());

        accountRepository.save(account);
        logger.info("AccountId "+openAccountCommand.getAccountId()+ " saved in the Database successfully...");
        return account.getWallets().stream()
                .map(Wallet::getWalletId)
                .collect(Collectors.toList());

    }

    private void changeAccountStatus(ChangeAccountStatusCommand changeAccountStatusCommand) {
        Account account = accountRepository.findById(changeAccountStatusCommand.getAccountId()).orElseThrow(() -> new AccountNotFoundException(changeAccountStatusCommand.getAccountId()));

        switch (changeAccountStatusCommand.getStatus()) {
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
            default -> throw new InvalidAccountStatusException(changeAccountStatusCommand.getStatus());
        }

        // Save Account entity to DB
        accountRepository.save(account);
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