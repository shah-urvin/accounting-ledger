package com.cg.account.transaction;

import com.cg.account.command.model.WalletModel;

import java.math.BigDecimal;

public interface WalletOperations {
    void debit(WalletModel wallet, BigDecimal txnAmount);
    void credit(WalletModel fromWallet, WalletModel toWallet, BigDecimal txnAmount,String symbol);
}
