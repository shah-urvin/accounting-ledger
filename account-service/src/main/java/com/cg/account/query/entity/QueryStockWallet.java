package com.cg.account.query.entity;

import com.cg.account.constants.AssetType;
import com.cg.account.constants.StockSymbol;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "query_stock_wallet")
@NoArgsConstructor
public class QueryStockWallet extends QueryWallet {
    @Column(name = "stock_symbol")
    @Enumerated(EnumType.STRING)
    private StockSymbol stockSymbol;

    @Column(name = "balance_qty")
    private Long balanceQty;

    @Builder
    public QueryStockWallet(String walletId, AccountQuery account, StockSymbol stockSymbol, Long balanceQty, LocalDateTime timestamp) {
        super(walletId,account, AssetType.STOCK,timestamp);
        this.stockSymbol=stockSymbol;
        this.balanceQty = balanceQty;
    }
}
