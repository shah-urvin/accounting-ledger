package com.cg.account.aggregate;

import com.cg.account.command.ChangeAccountStatusCommand;
import com.cg.account.command.OpenAccountCommand;
import com.cg.account.command.ProcessUpdateAccountCommand;
import com.cg.account.command.model.*;
import com.cg.account.constants.AccountStatus;
import com.cg.account.constants.FiatCurrency;
import com.cg.account.event.AccountOpenedEvent;
import com.cg.account.event.AccountStatusChangedEvent;
import com.cg.account.event.AccountUpdateProcessedEvent;
import com.cg.account.repository.AccountRepository;
import com.cg.account.transaction.WalletOperations;
import com.cg.account.transaction.factory.WalletOperationsFactory;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.cg.account.constants.CryptoType.*;
import static com.cg.account.constants.FundType.*;
import static com.cg.account.constants.StockSymbol.*;
import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class AccountAggregate {

    @AggregateIdentifier
    private String accountId;
    private AccountStatus accountStatus;
    private Map<String, WalletModel> wallets = new HashMap<>();

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
                .wallets(wallets)
                .build());
    }

    @EventSourcingHandler
    public void on(AccountUpdateProcessedEvent accountUpdateProcessedEvent) {
        logger.info("AccountUpdateProcessedEvent event invoked...");
        this.accountId = accountUpdateProcessedEvent.getAccountId();
        this.accountStatus =accountUpdateProcessedEvent.getAccountStatus();
        this.wallets = accountUpdateProcessedEvent.getWallets();
    }

    private void openAccount(OpenAccountCommand openAccountCommand) {
        // Create Account
        // Create 5 Wallets (USDWallet,HKDWallet,CryptoWallet,StockWallet
        // Adding USD Wallet
        String usdWalletId = UUID.randomUUID().toString();
        this.wallets.put(usdWalletId, USDWalletModel.builder()
                .accountId(openAccountCommand.getAccountId())
                .walletId(usdWalletId.toString())
                .balance(FiatCurrency.USD.getInitialBalance()).build());

        // Adding HKD Wallet
        String hkdWalletId = UUID.randomUUID().toString();
        this.wallets.put(hkdWalletId, HKDWalletModel.builder()
                .accountId(openAccountCommand.getAccountId())
                .walletId(hkdWalletId.toString())
                .balance(FiatCurrency.HKD.getInitialBalance()).build());

        // Adding the Crypto Wallet
        String cryptoWalletId = UUID.randomUUID().toString();
        this.wallets.put(cryptoWalletId, CryptoWalletModel.builder()
                .accountId(openAccountCommand.getAccountId())
                .walletId(cryptoWalletId.toString())
                .cryptoData(getInitialCryptoDataModel())
                .build());

        // Adding the Stock Wallet
        String stockWalletId = UUID.randomUUID().toString();
        this.wallets.put(stockWalletId, StockWalletModel.builder()
                .accountId(openAccountCommand.getAccountId())
                .walletId(stockWalletId.toString())
                .stockData(getInitialStockDataModel())
                .build());

        // Adding the Fund Wallet
        String fundWalletId = UUID.randomUUID().toString();
        this.wallets.put(fundWalletId,FundWalletModel.builder()
                .accountId(openAccountCommand.getAccountId())
                .walletId(fundWalletId)
                .fundData(getInitialFundDataModel())
                .build());

        logger.info("AccountId "+openAccountCommand.getAccountId()+ " all Wallets been created...");
    }

    /**
     * Create Initial CryptoDataModelMap
     * @return
     */
    private Map<String,CryptoDataModel> getInitialCryptoDataModel() {
        Map<String,CryptoDataModel> cryptoDataModelap = new HashMap<>();
        cryptoDataModelap.put(BTC.getSymbol(),CryptoDataModel.builder()
                .cryptoType(BTC)
                .balance(BTC.getInitialBalance())
                .build());
        cryptoDataModelap.put(ETH.getSymbol(),CryptoDataModel.builder()
                .cryptoType(ETH)
                .balance(ETH.getInitialBalance())
                .build());
        cryptoDataModelap.put(SHIB.getSymbol(),CryptoDataModel.builder()
                .cryptoType(SHIB)
                .balance(SHIB.getInitialBalance())
                .build());
        return cryptoDataModelap;
    }

    /**
     * getInitialStockDataModel initialize the StockDataModel
     * @return
     */
    private Map<String,StockDataModel> getInitialStockDataModel() {
        Map<String,StockDataModel> stockDataModelMap = new HashMap<>();
        stockDataModelMap.put(CAPGEMINI.getSymbol(),StockDataModel.builder()
                .stockSymbol(CAPGEMINI)
                .balanceQty(CAPGEMINI.getInitialBalance())
                .build());
        stockDataModelMap.put(HSBC.getSymbol(),StockDataModel.builder()
                .stockSymbol(CAPGEMINI)
                .balanceQty(CAPGEMINI.getInitialBalance())
                .build());
        stockDataModelMap.put(CITI.getSymbol(),StockDataModel.builder()
                .stockSymbol(CAPGEMINI)
                .balanceQty(CAPGEMINI.getInitialBalance())
                .build());
        return stockDataModelMap;
    }

    /**
     * getInitialFundDataModel provide initial FundDataModel
     * @return
     */
    private Map<String,FundDataModel> getInitialFundDataModel() {
        Map<String,FundDataModel> fundTypeFundDataModelMap = new HashMap<>();
        fundTypeFundDataModelMap.put(HSBC_SMALL_CAP.getSymbol(), FundDataModel.builder()
                .fundType(HSBC_SMALL_CAP)
                .balance(HSBC_SMALL_CAP.getInitialBalance())
                .build());
        fundTypeFundDataModelMap.put(AIA_MID_CAP.getSymbol(), FundDataModel.builder()
                .fundType(AIA_MID_CAP)
                .balance(AIA_MID_CAP.getInitialBalance())
                .build());
        fundTypeFundDataModelMap.put(CITI_LARGE_CAP.getSymbol(), FundDataModel.builder()
                .fundType(CITI_LARGE_CAP)
                .balance(CITI_LARGE_CAP.getInitialBalance())
                .build());
        return fundTypeFundDataModelMap;
    }

    /**
     * processWalletBalance method calculate the balance and update the balance
     * @param processUpdateAccountCommand
     */
    private void processWalletBalance(ProcessUpdateAccountCommand processUpdateAccountCommand) {
        WalletModel fromWalletModel = wallets.get(processUpdateAccountCommand.getFromWalletId());
        WalletModel toWalletModel = wallets.get(processUpdateAccountCommand.getToWalletId());
        // Retrieve appropriate debitOperation or creditOperation object
        WalletOperations debitOperation = WalletOperationsFactory.getWalletOperations(fromWalletModel.getAssetType());
        WalletOperations creditOperations = WalletOperationsFactory.getWalletOperations(toWalletModel.getAssetType());

        // Perform debit and credit Operations
        debitOperation.debit(wallets.get(processUpdateAccountCommand.getFromWalletId()), processUpdateAccountCommand.getTxnAmount());
        creditOperations.credit(wallets.get(processUpdateAccountCommand.getFromWalletId())
                ,wallets.get(processUpdateAccountCommand.getToWalletId())
                ,processUpdateAccountCommand.getTxnAmount()
                ,processUpdateAccountCommand.getToSymbol());

        // Update the current state of the Account;
        this.accountId = processUpdateAccountCommand.getAccountId();
        this.accountStatus = processUpdateAccountCommand.getAccountStatus();
        wallets.put(fromWalletModel.getWalletId(),fromWalletModel);
        wallets.put(toWalletModel.getWalletId(),toWalletModel);
    }

}