package com.cg.account.transaction;

import com.cg.account.constants.FiatCurrencyRateConstants;
import com.cg.account.entity.*;
import com.cg.account.exception.InsufficientBalanceException;
import com.cg.account.posting.dto.WalletChangeDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class USDWalletOperations implements WalletOperations{

    @Override
    public void debit(Wallet wallet, BigDecimal txnAmount) {
        USDWallet usdWallet = (USDWallet) wallet;
        if (usdWallet.getBalance().compareTo(txnAmount) >=0) {
            usdWallet.setBalance((usdWallet.getBalance().subtract(txnAmount)));
        } else {
            throw new InsufficientBalanceException(usdWallet.getWalletId());
        }
    }

    @Override
    public void credit(Wallet fromWallet,Wallet toWallet,BigDecimal txnAmount) {
        USDWallet usdWallet = (USDWallet) toWallet;
        switch (fromWallet.getAssetType()) {
            case FIAT_HKD -> {
                usdWallet.setBalance(usdWallet.getBalance().add(FiatCurrencyRateConstants.HKD_TO_USD.multiply(txnAmount)));
                break;
            }
            case CRYPTO -> {
                CryptoWallet cryptoWallet = (CryptoWallet) fromWallet;
                usdWallet.setBalance(usdWallet.getBalance().add(cryptoWallet.getCryptoType().getUsdRate().multiply(txnAmount)));
                break;
            }
            case STOCK -> {
                StockWallet stockWallet = (StockWallet) fromWallet;
                usdWallet.setBalance(usdWallet.getBalance().add(stockWallet.getStockSymbol().getUsdRate().multiply(txnAmount)));
                break;
            }
            case FUND -> {
                FundWallet fundWallet = (FundWallet) fromWallet;
                usdWallet.setBalance(usdWallet.getBalance().add(fundWallet.getFundName().getUsdRate().multiply(txnAmount)));
                break;
            }
        }
    }


}
