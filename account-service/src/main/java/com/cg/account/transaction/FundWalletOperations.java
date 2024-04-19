package com.cg.account.transaction;

import com.cg.account.command.model.WalletModel;
import com.cg.account.entity.Wallet;
import com.cg.account.posting.dto.WalletChangeDTO;

import java.math.BigDecimal;

public class FundWalletOperations implements WalletOperations{


    @Override
    public void debit(WalletModel wallet, BigDecimal txnAmount) {

    }

    @Override
    public void credit(WalletModel fromWallet,WalletModel toWallet,BigDecimal txnAmount,String symbol) {

    }
}
