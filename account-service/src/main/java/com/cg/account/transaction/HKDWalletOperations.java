package com.cg.account.transaction;

import com.cg.account.constants.FiatCurrencyRateConstants;
import com.cg.account.entity.*;
import com.cg.account.exception.InsufficientBalanceException;
import com.cg.account.posting.dto.WalletChangeDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HKDWalletOperations implements WalletOperations {

    @Override
    public void debit(Wallet wallet, BigDecimal txnAmount) {
        HKDWallet hkdWallet = (HKDWallet) wallet;
        if (hkdWallet.getBalance().compareTo(txnAmount) >=0) {
            hkdWallet.setBalance((hkdWallet.getBalance().subtract(txnAmount)));
        } else {
            throw new InsufficientBalanceException(hkdWallet.getWalletId());
        }

    }

    @Override
    public void credit(Wallet fromWallet,Wallet toWallet,BigDecimal txnAmount) {
        HKDWallet hkdWallet = (HKDWallet) toWallet;
        switch (fromWallet.getAssetType()) {
            case FIAT_USD -> {
                hkdWallet.setBalance(hkdWallet.getBalance().add(FiatCurrencyRateConstants.USD_TO_HKD.multiply(txnAmount)));
                break;
            }
            case CRYPTO -> {
                CryptoWallet cryptoWallet = (CryptoWallet) fromWallet;
                hkdWallet.setBalance(hkdWallet.getBalance().add(cryptoWallet.getCryptoType().getHkdRate().multiply(txnAmount)));
                break;
            }
            case STOCK -> {
                StockWallet stockWallet = (StockWallet) fromWallet;
                hkdWallet.setBalance(hkdWallet.getBalance().add(stockWallet.getStockSymbol().getHkdRate().multiply(txnAmount)));
                break;
            }
            case FUND -> {
                FundWallet fundWallet = (FundWallet) fromWallet;
                hkdWallet.setBalance(hkdWallet.getBalance().add(fundWallet.getFundName().getHkdRate().multiply(txnAmount)));
                break;
            }
        }
    }
}
