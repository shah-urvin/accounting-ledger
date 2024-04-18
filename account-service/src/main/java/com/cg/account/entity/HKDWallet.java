package com.cg.account.entity;

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
@Table(name="hkd_wallet")
@NoArgsConstructor
public class HKDWallet extends Wallet{
    @Column(name = "balance")
    private BigDecimal balance;

    @Builder
    public HKDWallet(String walletId, Account account, BigDecimal balance, LocalDateTime timestamp) {
        super(walletId,account, AssetType.FIAT_HKD,timestamp);
        this.balance=balance;
    }
}
