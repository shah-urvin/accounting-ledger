package com.cg.account.transaction;

import com.cg.account.command.model.*;
import com.cg.account.exception.InsufficientBalanceException;
import com.cg.account.exception.InvalidPostingSymbolException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FundWalletOperations implements WalletOperations{


    @Override
    public void debit(WalletModel wallet, BigDecimal txnAmount,String symbol) {
        FundWalletModel fundWalletModel = (FundWalletModel) wallet;
        FundDataModel fundDataModel = fundWalletModel.getFundData().get(symbol);
        if(fundDataModel != null && fundDataModel.getBalance().compareTo(txnAmount) >=0) {
            fundDataModel.setBalance(fundDataModel.getBalance().subtract(txnAmount));
            fundWalletModel.getFundData().put(symbol,fundDataModel);
        } else {
            throw new InsufficientBalanceException(fundWalletModel.getWalletId());
        }
    }

    @Override
    public void credit(WalletModel fromWallet,WalletModel toWallet,BigDecimal txnAmount,String symbol) {
        FundWalletModel fundWalletModel = (FundWalletModel) toWallet;
        FundDataModel fundDataModel = fundWalletModel.getFundData().get(symbol);
        if(fundWalletModel != null && fundDataModel != null) {
            switch (fromWallet.getAssetType()) {
                case FIAT_HKD -> {
                    HKDWalletModel hkdWalletModel = (HKDWalletModel) fromWallet;
                    if(hkdWalletModel != null) {
                        fundDataModel.setBalance(fundDataModel.getBalance().add(txnAmount.divide(fundDataModel.getFundType().getHkdRate(),7, RoundingMode.HALF_UP)));
                        fundWalletModel.getFundData().put(symbol,fundDataModel);
                    }
                }
                case FIAT_USD -> {
                    USDWalletModel usdWalletModel = (USDWalletModel) fromWallet;
                    if(usdWalletModel != null) {
                        fundDataModel.setBalance(fundDataModel.getBalance().add(txnAmount.divide(fundDataModel.getFundType().getUsdRate(),7, RoundingMode.HALF_UP)));
                        fundWalletModel.getFundData().put(symbol,fundDataModel);
                    }
                }
                default -> new InvalidPostingSymbolException(symbol);
            }
        }
    }
}
