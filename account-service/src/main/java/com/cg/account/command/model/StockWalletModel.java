package com.cg.account.command.model;

import com.cg.account.constants.AssetType;
import com.cg.account.constants.StockSymbol;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockWalletModel extends WalletModel {
    private StockSymbol stockSymbol;
    private BigDecimal balanceQty;

    @Builder
    public StockWalletModel(String walletId, String accountId, StockSymbol stockSymbol, BigDecimal balanceQty) {
        super(walletId,accountId, AssetType.STOCK);
        this.stockSymbol=stockSymbol;
        this.balanceQty = balanceQty;
    }
}
