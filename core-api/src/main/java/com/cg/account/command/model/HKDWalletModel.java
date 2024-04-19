package com.cg.account.command.model;

import com.cg.account.constants.AssetType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HKDWalletModel extends WalletModel {
    private BigDecimal balance;

    @Builder
    public HKDWalletModel(String walletId, String accountId, BigDecimal balance) {
        super(walletId,accountId, AssetType.FIAT_HKD);
        this.balance=balance;
    }
}
