package com.cg.account.transaction;

import com.cg.account.command.model.*;
import com.cg.account.exception.InsufficientBalanceException;
import com.cg.account.exception.InvalidPostingSymbolException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StockWalletOperations implements WalletOperations{


    @Override
    public void debit(WalletModel wallet, BigDecimal txnAmount,String symbol) {
        StockWalletModel stockWalletModel = (StockWalletModel) wallet;
        StockDataModel stockDataModel = stockWalletModel.getStockData().get(symbol);

        if(stockDataModel != null && stockDataModel.getBalanceQty().compareTo(txnAmount) >= 0) {
            stockDataModel.setBalanceQty(stockDataModel.getBalanceQty().subtract(txnAmount));
            stockWalletModel.getStockData().put(symbol,stockDataModel);
        }else {
            throw new InsufficientBalanceException(stockWalletModel.getWalletId());
        }
    }

    @Override
    public void credit(WalletModel fromWallet,WalletModel toWallet,BigDecimal txnAmount,String symbol) {
        StockWalletModel stockWalletModel = (StockWalletModel) toWallet;
        if(stockWalletModel != null) {
            switch (fromWallet.getAssetType()) {
                case FIAT_HKD -> {
                    HKDWalletModel hkdWalletModel = (HKDWalletModel) fromWallet;
                    if(hkdWalletModel != null) {
                        StockDataModel stockDataModel = stockWalletModel.getStockData().get(symbol);
                        if (stockDataModel != null) {
                            stockDataModel.setBalanceQty(stockDataModel.getBalanceQty().add(txnAmount.divide(stockDataModel.getStockSymbol().getHkdRate(),7, RoundingMode.HALF_UP)));
                            stockWalletModel.getStockData().put(symbol,stockDataModel);
                        }
                    }
                }
                case FIAT_USD -> {
                    USDWalletModel usdWalletModel = (USDWalletModel) fromWallet;
                    if(usdWalletModel != null) {
                        StockDataModel stockDataModel = stockWalletModel.getStockData().get(symbol);
                        if (stockDataModel != null) {
                            stockDataModel.setBalanceQty(stockDataModel.getBalanceQty().add(txnAmount.divide(stockDataModel.getStockSymbol().getUsdRate(),7, RoundingMode.HALF_UP)));
                            stockWalletModel.getStockData().put(symbol,stockDataModel);
                        }
                    }
                }
                default -> new InvalidPostingSymbolException(symbol);
            }
        }
    }
}
