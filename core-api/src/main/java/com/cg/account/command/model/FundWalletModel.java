package com.cg.account.command.model;

import com.cg.account.constants.AssetType;
import lombok.Builder;
import lombok.Data;

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
