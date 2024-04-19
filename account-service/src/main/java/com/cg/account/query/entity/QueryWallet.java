package com.cg.account.query.entity;

import com.cg.account.constants.AssetType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
@Table(name = "query_wallet")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class QueryWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="wallet_id")
    private String walletId;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountQuery account;

    @Column(name = "asset_type")
    @Enumerated(EnumType.STRING)
    private AssetType assetType;

    @Column(name = "time_stamp")
    private LocalDateTime timestamp;

    public QueryWallet() {}

    public QueryWallet(String walletId, AccountQuery account, AssetType assetType, LocalDateTime timestamp) {
        this.walletId = walletId;
        this.account = account;
        this.assetType = assetType;
        this.timestamp = timestamp;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryWallet wallet = (QueryWallet) o;
        return walletId.equals(wallet.walletId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(walletId);
    }
}
