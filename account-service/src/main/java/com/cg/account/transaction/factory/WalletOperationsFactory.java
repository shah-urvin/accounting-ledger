package com.cg.account.transaction.factory;

import com.cg.account.transaction.*;
import com.cg.account.constants.AssetType;

public class WalletOperationsFactory {
    public static WalletOperations getWalletOperations(AssetType assetType) {
        switch (assetType) {
            case FIAT_HKD -> {
                return new HKDWalletOperations();
            }
            case FIAT_USD -> {
                return new USDWalletOperations();
            }
            case CRYPTO -> {
                return new CryptoWalletOperations();
            }
            case STOCK -> {
                return new StockWalletOperations();
            }
            case FUND -> {
                return new FundWalletOperations();
            }
            default -> throw new IllegalStateException("Invalid AssetType value: " + assetType);
        }
    }
}
