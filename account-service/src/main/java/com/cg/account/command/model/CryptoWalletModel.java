package com.cg.account.command.model;

import com.cg.account.constants.AssetType;
import com.cg.account.constants.CryptoType;
import com.cg.account.entity.Account;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CryptoWalletModel extends WalletModel {
    private CryptoType cryptoType;
    private BigDecimal balanceQty;

    @Builder
    public CryptoWalletModel(String walletId, String accountId, CryptoType cryptoType, BigDecimal balanceQty) {
        super(walletId,accountId, AssetType.CRYPTO);
        this.cryptoType = cryptoType;
        this.balanceQty = balanceQty;
    }
}
