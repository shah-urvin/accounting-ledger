package com.cg.account.query.entity;

import com.cg.account.constants.AssetType;
import com.cg.account.constants.FundType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "query_fund_wallet")
@NoArgsConstructor
public class QueryFundWallet extends QueryWallet {
    @Column(name = "fund_name")
    @Enumerated(EnumType.STRING)
    private FundType fundName;

    @Column(name = "balance")
    private BigDecimal balance;

    @Builder
    public QueryFundWallet(String walletId, AccountQuery account, FundType fundName, BigDecimal balance, LocalDateTime timestamp) {
        super(walletId,account, AssetType.FUND,timestamp);
        this.fundName = fundName;
        this.balance = balance;
    }
}
