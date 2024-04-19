package com.cg.account.command.model;

import com.cg.account.constants.AssetType;
import com.cg.account.constants.CryptoType;
import com.cg.account.constants.FundType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class FundWalletModel extends WalletModel {
    private Map<String, FundDataModel> fundData;

    @Builder
    public FundWalletModel(String walletId, String accountId, Map<String, FundDataModel> fundData) {
        super(walletId,accountId, AssetType.FUND);
        this.fundData = fundData;
    }
}
