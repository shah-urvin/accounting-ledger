package com.cg.account.aggregate;

import com.cg.account.command.ChangeAccountStatusCommand;
import com.cg.account.command.ChangeWalletBalanceCommand;
import com.cg.account.command.OpenAccountCommand;
import com.cg.account.command.ProcessUpdateAccountCommand;
import com.cg.account.command.model.*;
import com.cg.account.constants.*;
import com.cg.account.entity.*;
import com.cg.account.event.AccountOpenedEvent;
import com.cg.account.event.AccountStatusChangedEvent;
import com.cg.account.event.AccountUpdateProcessedEvent;
import com.cg.account.event.WalletBalanceChangedEvent;
import com.cg.account.exception.AccountNotFoundException;
import com.cg.account.exception.InvalidAccountStatusException;
import com.cg.account.exception.WalletNotFoundException;
import com.cg.account.posting.dto.WalletChangeDTO;
import com.cg.account.repository.AccountRepository;
import com.cg.account.transaction.WalletOperations;
import com.cg.account.transaction.factory.WalletOperationsFactory;
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

import static com.cg.account.constants.AssetType.*;
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
    public void handle(ChangeAccountStatusCommand changeAccountStatusCommand) {
        this.accountRepository = accountRepository;
        logger.info("ChangeAccountStatusCommand get called..."+changeAccountStatusCommand.getAccountId());
        apply(AccountStatusChangedEvent.builder()
                .accountId(changeAccountStatusCommand.getAccountId())
                .status(changeAccountStatusCommand.getStatus().equals(AccountStatus.RELEASE)?AccountStatus.OPEN:changeAccountStatusCommand.getStatus()).build());
    }

    @EventSourcingHandler
    public void on(AccountStatusChangedEvent accountStatusChangedEvent) {
        logger.info("AccountStatusChangedEvent got called from AccountAggregate...");
        this.accountId=accountStatusChangedEvent.getAccountId();
        this.accountStatus=accountStatusChangedEvent.getStatus();
    }

    @CommandHandler
    public void on(ProcessUpdateAccountCommand processUpdateAccountCommand,AccountRepository accountRepository) {
        logger.info("ProcessUpdateAccountCommand invoked...");
        this.accountRepository = accountRepository;
        this.accountId = processUpdateAccountCommand.getAccountId();
        processWalletBalance(processUpdateAccountCommand);
        apply(AccountUpdateProcessedEvent.builder()
                .accountId(processUpdateAccountCommand.getAccountId())
                .postingId(processUpdateAccountCommand.getPostingId())
                .fromWalletId(processUpdateAccountCommand.getFromWalletId())
                .toWalletId(processUpdateAccountCommand.getToWalletId())
                .txnAmount(processUpdateAccountCommand.getTxnAmount())
                .build());
    }

    @EventSourcingHandler
    public void on(AccountUpdateProcessedEvent accountUpdateProcessedEvent) {
        logger.info("AccountUpdateProcessedEvent event invoked...");
        this.accountId = accountUpdateProcessedEvent.getAccountId();
        this.accountStatus =accountUpdateProcessedEvent.getAccountStatus();
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
        this.wallets.put(FIAT_HKD, HKDWalletModel.builder()
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

    /**
     * processWalletBalance method calculate the balance and update the balance
     * @param processUpdateAccountCommand
     */
    private void processWalletBalance(ProcessUpdateAccountCommand processUpdateAccountCommand) {
        Account account = accountRepository.findById(processUpdateAccountCommand.getAccountId()).orElseThrow(() -> new AccountNotFoundException(processUpdateAccountCommand.getAccountId()));
        Wallet fromWallet = getWalletByWalletId(account.getWallets(),processUpdateAccountCommand.getFromWalletId());
        Wallet toWallet = getWalletByWalletId(account.getWallets(),processUpdateAccountCommand.getToWalletId());
        WalletOperations fromWalletOperations = WalletOperationsFactory.getWalletOperations(fromWallet.getAssetType());
        WalletOperations toWalletOperations = WalletOperationsFactory.getWalletOperations(toWallet.getAssetType());

        // Perform debit and credit Operations
        fromWalletOperations.debit(fromWallet, processUpdateAccountCommand.getTxnAmount());
        toWalletOperations.credit(fromWallet,toWallet,processUpdateAccountCommand.getTxnAmount());
        account.getWallets().add(fromWallet);
        account.getWallets().add(toWallet);
        accountRepository.save(account);

        // Update the current state of the Account;
        this.accountId = processUpdateAccountCommand.getAccountId();
        this.accountStatus = processUpdateAccountCommand.getAccountStatus();
        updateWallets((account.getWallets()));
    }

    /**
     * This method updates the Wallet with respective AssetType
     * @param lstWallets
     */
    private void updateWallets(List<Wallet> lstWallets) {
        lstWallets.stream().forEach(wallet -> {
            switch (wallet.getAssetType()) {
                case FIAT_HKD -> {
                    HKDWallet hkdWallet = (HKDWallet) wallet;
                    this.wallets.put(FIAT_HKD,HKDWalletModel.builder()
                            .accountId(hkdWallet.getAccount().getAccountId())
                            .walletId(hkdWallet.getWalletId())
                            .balance(hkdWallet.getBalance())
                            .build());
                }
                case FIAT_USD -> {
                    USDWallet usdWallet = (USDWallet) wallet;
                    this.wallets.put(FIAT_USD,USDWalletModel.builder()
                            .walletId(usdWallet.getWalletId())
                            .balance(usdWallet.getBalance())
                            .build());
                }
                case CRYPTO -> {
                    CryptoWallet cryptoWallet =(CryptoWallet) wallet;
                    this.wallets.put(CRYPTO,CryptoWalletModel.builder()
                            .accountId(cryptoWallet.getAccount().getAccountId())
                            .balanceQty(cryptoWallet.getBalanceQty())
                            .cryptoType(cryptoWallet.getCryptoType())
                            .build());

                }
                case STOCK -> {
                    StockWallet stockWallet =(StockWallet) wallet;
                    this.wallets.put(STOCK,StockWalletModel.builder()
                            .accountId(stockWallet.getAccount().getAccountId())
                            .walletId(stockWallet.getWalletId())
                            .balanceQty(stockWallet.getBalanceQty())
                            .stockSymbol(stockWallet.getStockSymbol())
                            .build());
                }
                case FUND -> {
                    FundWallet fundWallet = (FundWallet) wallet;
                    this.wallets.put(FUND,FundWalletModel.builder()
                            .accountId(fundWallet.getAccount().getAccountId())
                            .walletId(fundWallet.getWalletId())
                            .balance(fundWallet.getBalance())
                            .fundName(fundWallet.getFundName())
                            .build());
                }
            }
        });
    }

    private Wallet getWalletByWalletId(List<Wallet> lstWallets,String walletId) {
        return lstWallets.stream().filter(wallet -> wallet.getWalletId().equals(walletId)).findFirst().get();
    }

}