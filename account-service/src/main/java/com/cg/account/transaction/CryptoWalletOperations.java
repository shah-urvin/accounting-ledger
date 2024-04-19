package com.cg.account.transaction;

import com.cg.account.constants.AssetType;
import com.cg.account.entity.Wallet;
import com.cg.account.posting.dto.WalletChangeDTO;

import java.math.BigDecimal;

public class CryptoWalletOperations implements WalletOperations{

    @Override
    public void debit(Wallet wallet, BigDecimal txnAmount) {

    }

    @Override
    public void credit(Wallet fromWallet,Wallet toWallet,BigDecimal txnAmount) {

    }
}
