package com.cg.account.entity;

import com.cg.account.constants.AssetType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@Table(name="usd_wallet")
@NoArgsConstructor
public class USDWallet extends Wallet {
    @Column(name = "balance")
    private BigDecimal balance;

    @Builder
    public USDWallet(String walletId, Account account, BigDecimal balance) {
        super(walletId, account, AssetType.FIAT_USD);
        this.balance = balance;
    }
}