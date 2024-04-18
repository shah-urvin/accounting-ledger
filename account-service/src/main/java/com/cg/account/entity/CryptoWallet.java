package com.cg.account.entity;

import com.cg.account.constants.AssetType;
import com.cg.account.constants.CryptoType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "crypto_wallet")
@Data
@NoArgsConstructor
public class CryptoWallet extends Wallet{

    @Column(name = "crypto_type")
    @Enumerated(EnumType.STRING)
    private CryptoType cryptoType;

    @Column(name = "balance_qty")
    private BigDecimal balanceQty;

    @Builder
    public CryptoWallet(String walletId, Account account,CryptoType cryptoType,BigDecimal balanceQty) {
        super(walletId,account, AssetType.CRYPTO);
        this.cryptoType = cryptoType;
        this.balanceQty = balanceQty;
    }
}
