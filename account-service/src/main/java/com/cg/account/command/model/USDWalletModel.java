package com.cg.account.command.model;

import com.cg.account.constants.AssetType;
import com.cg.account.entity.Account;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class USDWalletModel extends WalletModel{
    private BigDecimal balance;

    @Builder
    public USDWalletModel(String walletId, String accountId, BigDecimal balance) {
        super(walletId, accountId, AssetType.FIAT_USD);
        this.balance = balance;
    }
}
