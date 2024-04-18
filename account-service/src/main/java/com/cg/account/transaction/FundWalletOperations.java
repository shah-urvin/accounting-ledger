package com.cg.account.transaction;

import com.cg.account.entity.Wallet;
import com.cg.account.posting.dto.WalletChangeDTO;
import com.cg.account.posting.entity.Posting;

import java.math.BigDecimal;

public class FundWalletOperations implements WalletOperations{


    @Override
    public WalletChangeDTO debit(Wallet wallet, BigDecimal txnAmount) {
        return null;
    }

    @Override
    public WalletChangeDTO credit(Posting posting) {
        return null;
    }
}
