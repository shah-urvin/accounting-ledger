package com.cg.account.command.model;

import com.cg.account.constants.AssetType;
import com.cg.account.constants.StockSymbol;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
public class StockWalletModel extends WalletModel {
    private Map<String,StockDataModel> stockData;

    @Builder
    public StockWalletModel(String walletId, String accountId, Map<String,StockDataModel> stockData) {
        super(walletId,accountId, AssetType.STOCK);
        this.stockData = stockData;
    }
}
