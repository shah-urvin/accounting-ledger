package com.cg.account.command.model;

import com.cg.account.constants.AssetType;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
public class CryptoWalletModel extends WalletModel {
    private Map<String,CryptoDataModel> cryptoData;

    @Builder
    public CryptoWalletModel(String walletId, String accountId, Map<String,CryptoDataModel> cryptoData) {
        super(walletId,accountId, AssetType.CRYPTO);
        this.cryptoData = cryptoData;
    }
}