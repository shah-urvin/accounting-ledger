package com.cg.account.query.entity;

import com.cg.account.constants.AssetType;
import com.cg.account.constants.CryptoType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "query_crypto_wallet")
@Data
@NoArgsConstructor
public class QueryCryptoWallet extends QueryWallet {

    @Column(name = "crypto_type")
    @Enumerated(EnumType.STRING)
    private CryptoType cryptoType;

    @Column(name = "balance_qty")
    private BigDecimal balanceQty;

    @Builder
    public QueryCryptoWallet(String walletId, AccountQuery account, CryptoType cryptoType, BigDecimal balanceQty, LocalDateTime timestamp) {
        super(walletId,account, AssetType.CRYPTO,timestamp);
        this.cryptoType = cryptoType;
        this.balanceQty = balanceQty;
    }
}
