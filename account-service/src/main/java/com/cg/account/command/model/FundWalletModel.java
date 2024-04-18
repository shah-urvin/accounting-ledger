package com.cg.account.command.model;

import com.cg.account.constants.AssetType;
import com.cg.account.constants.FundType;
import com.cg.account.entity.Account;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FundWalletModel extends WalletModel {
    private FundType fundName;
    private BigDecimal balance;

    @Builder
    public FundWalletModel(String walletId, String accountId, FundType fundName, BigDecimal balance) {
        super(walletId,accountId, AssetType.FUND);
        this.fundName = fundName;
        this.balance = balance;
    }
}
