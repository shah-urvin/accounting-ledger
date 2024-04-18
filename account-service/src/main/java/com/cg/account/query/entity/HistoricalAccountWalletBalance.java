package com.cg.account.query.entity;

import com.cg.account.constants.AssetType;
import com.cg.account.constants.CryptoType;
import com.cg.account.constants.FundType;
import com.cg.account.constants.StockSymbol;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "historical_account_balance")
public class HistoricalAccountWalletBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "wallet_id")
    private String walletId;

    @Column(name = "asset_type")
    private AssetType assetType;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "stock_symbol")
    private StockSymbol stockSymbol;

    @Column(name="func_type")
    private FundType fundType;

    @Column(name = "crypto_type")
    private CryptoType cryptoType;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;




}
