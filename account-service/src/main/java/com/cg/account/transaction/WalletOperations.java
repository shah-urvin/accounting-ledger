package com.cg.account.transaction;

import com.cg.account.entity.Wallet;
import com.cg.account.posting.dto.WalletChangeDTO;

import java.math.BigDecimal;

public interface WalletOperations {
    void debit(Wallet wallet, BigDecimal txnAmount);
    void credit(Wallet fromWallet, Wallet toWallet, BigDecimal txnAmount);
}
