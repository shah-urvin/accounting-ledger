package com.cg.account.transaction;

import com.cg.account.entity.Wallet;
import com.cg.account.posting.dto.WalletChangeDTO;
import com.cg.account.posting.entity.Posting;

import java.math.BigDecimal;

public interface WalletOperations {
    WalletChangeDTO debit(Wallet wallet, BigDecimal txnAmount);
    WalletChangeDTO credit(Posting posting);
}
