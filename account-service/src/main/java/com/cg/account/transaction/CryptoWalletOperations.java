package com.cg.account.transaction;

import com.cg.account.command.model.*;
import com.cg.account.exception.InsufficientBalanceException;
import com.cg.account.exception.InvalidPostingSymbolException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CryptoWalletOperations implements WalletOperations{

    @Override
    public void debit(WalletModel wallet, BigDecimal txnAmount,String symbol) {
        CryptoWalletModel cryptoWalletModel = (CryptoWalletModel) wallet;
        CryptoDataModel cryptoDataModel = cryptoWalletModel.getCryptoData().get(symbol);
        if(cryptoDataModel !=null && cryptoDataModel.getBalance().compareTo(txnAmount) >= 0) {
            cryptoDataModel.setBalance(cryptoDataModel.getBalance().subtract(txnAmount));
            cryptoWalletModel.getCryptoData().put(symbol,cryptoDataModel);
        }else {
            throw new InsufficientBalanceException(cryptoWalletModel.getWalletId());
        }
    }

    @Override
    public void credit(WalletModel fromWallet,WalletModel toWallet,BigDecimal txnAmount,String symbol) {
        CryptoWalletModel cryptoWalletModel = (CryptoWalletModel) toWallet;
        CryptoDataModel cryptoDataModel = cryptoWalletModel.getCryptoData().get(symbol);
        if(cryptoWalletModel != null && cryptoDataModel != null) {
            switch (fromWallet.getAssetType()) {
                case FIAT_HKD -> {
                    HKDWalletModel hkdWalletModel = (HKDWalletModel) fromWallet;
                    if(hkdWalletModel != null) {
                        cryptoDataModel.setBalance(cryptoDataModel.getBalance().add(txnAmount.divide(cryptoDataModel.getCryptoType().getHkdRate(),7,RoundingMode.HALF_UP)));
                        cryptoWalletModel.getCryptoData().put(symbol,cryptoDataModel);
                    }
                }
                case FIAT_USD -> {
                    USDWalletModel usdWalletModel = (USDWalletModel) fromWallet;
                    if(usdWalletModel != null) {
                        cryptoDataModel.setBalance(cryptoDataModel.getBalance().add(txnAmount.divide(cryptoDataModel.getCryptoType().getUsdRate(),7, RoundingMode.HALF_UP)));
                        cryptoWalletModel.getCryptoData().put(symbol,cryptoDataModel);
                    }
                }
                default -> new InvalidPostingSymbolException(symbol);
            }
        }
    }
}
