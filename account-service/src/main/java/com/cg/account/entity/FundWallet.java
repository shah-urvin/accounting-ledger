package com.cg.account.entity;

import com.cg.account.constants.AssetType;
import com.cg.account.constants.FundType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@Table
@NoArgsConstructor
public class FundWallet extends Wallet{
    @Column(name = "fund_name")
    private FundType fundName;

    @Column(name = "balance")
    private BigDecimal balance;

    @Builder
    public FundWallet(String walletId, Account account, FundType fundName,BigDecimal balance) {
        super(walletId,account, AssetType.FUND);
        this.fundName = fundName;
        this.balance = balance;
    }
}
