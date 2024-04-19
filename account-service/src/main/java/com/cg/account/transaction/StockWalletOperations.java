package com.cg.account.transaction;

import com.cg.account.command.model.WalletModel;

import java.math.BigDecimal;

public class StockWalletOperations implements WalletOperations{


    @Override
    public void debit(WalletModel wallet, BigDecimal txnAmount) {

    }

    @Override
    public void credit(WalletModel fromWallet,WalletModel toWallet,BigDecimal txnAmount,String symbol) {

    }
}
