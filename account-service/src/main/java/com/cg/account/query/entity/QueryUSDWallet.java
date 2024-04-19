package com.cg.account.query.entity;

import com.cg.account.constants.AssetType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name="query_usd_wallet")
@NoArgsConstructor
public class QueryUSDWallet extends QueryWallet {
    @Column(name = "balance")
    private BigDecimal balance;

    @Builder
    public QueryUSDWallet(String walletId, AccountQuery account, BigDecimal balance, LocalDateTime timestamp) {
        super(walletId, account, AssetType.FIAT_USD,timestamp);
        this.balance = balance;
    }
}