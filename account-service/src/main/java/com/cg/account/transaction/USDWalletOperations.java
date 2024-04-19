package com.cg.account.transaction;

import com.cg.account.command.model.*;
import com.cg.account.constants.FiatCurrencyRateConstants;
import com.cg.account.exception.InsufficientBalanceException;

import java.math.BigDecimal;

public class USDWalletOperations implements WalletOperations{

    @Override
    public void debit(WalletModel wallet, BigDecimal txnAmount) {
        USDWalletModel usdWallet = (USDWalletModel) wallet;
        if (usdWallet.getBalance().compareTo(txnAmount) >=0) {
            usdWallet.setBalance((usdWallet.getBalance().subtract(txnAmount)));
        } else {
            throw new InsufficientBalanceException(usdWallet.getWalletId());
        }
    }

    @Override
    public void credit(WalletModel fromWallet, WalletModel toWallet, BigDecimal txnAmount,String symbol) {
        USDWalletModel usdWallet = (USDWalletModel) toWallet;
        switch (fromWallet.getAssetType()) {
            case FIAT_HKD -> {
                usdWallet.setBalance(usdWallet.getBalance().add(FiatCurrencyRateConstants.HKD_TO_USD.multiply(txnAmount)));
                break;
            }
            case CRYPTO -> {
                CryptoWalletModel cryptoWallet = (CryptoWalletModel) fromWallet;
                usdWallet.setBalance(usdWallet.getBalance().add(cryptoWallet.getCryptoData().get(symbol).getCryptoType().getUsdRate().multiply(txnAmount)));
                break;
            }
            case STOCK -> {
                StockWalletModel stockWallet = (StockWalletModel) fromWallet;
                usdWallet.setBalance(usdWallet.getBalance().add(stockWallet.getStockData().get(symbol).getStockSymbol().getUsdRate().multiply(txnAmount)));
                break;
            }
            case FUND -> {
                FundWalletModel fundWallet = (FundWalletModel) fromWallet;
                usdWallet.setBalance(usdWallet.getBalance().add(fundWallet.getFundData().get(symbol).getFundType().getUsdRate().multiply(txnAmount)));
                break;
            }
        }
    }
}
