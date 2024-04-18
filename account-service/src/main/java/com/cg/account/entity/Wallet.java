package com.cg.account.entity;

import com.cg.account.constants.AssetType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
public abstract class Wallet {
    @Id
    @Column(name="wallet_id")
    private String walletId;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "asset_type")
    @Enumerated(EnumType.STRING)
    private AssetType assetType;

    public Wallet() {}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return walletId.equals(wallet.walletId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(walletId);
    }
}
