package com.cg.account.command.model;

import com.cg.account.constants.AssetType;
import com.cg.account.constants.StockSymbol;
import com.cg.account.entity.Account;
import lombok.Builder;
import lombok.Data;

@Data
public class StockWalletModel extends WalletModel {
    private StockSymbol stockSymbol;
    private Long balanceQty;

    @Builder
    public StockWalletModel(String walletId, String accountId, StockSymbol stockSymbol, Long balanceQty) {
        super(walletId,accountId, AssetType.STOCK);
        this.stockSymbol=stockSymbol;
        this.balanceQty = balanceQty;
    }
}
