package com.cg.account.transaction;

import com.cg.account.command.model.*;
import com.cg.account.constants.FiatCurrencyRateConstants;
import com.cg.account.entity.*;
import com.cg.account.exception.InsufficientBalanceException;
import com.cg.account.posting.dto.WalletChangeDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HKDWalletOperations implements WalletOperations {

    @Override
    public void debit(WalletModel wallet, BigDecimal txnAmount) {
        HKDWalletModel hkdWallet = (HKDWalletModel) wallet;
        if (hkdWallet.getBalance().compareTo(txnAmount) >=0) {
            hkdWallet.setBalance((hkdWallet.getBalance().subtract(txnAmount)));
        } else {
            throw new InsufficientBalanceException(hkdWallet.getWalletId());
        }

    }

    @Override
    public void credit(WalletModel fromWallet, WalletModel toWallet, BigDecimal txnAmount, String symbol) {
        HKDWalletModel hkdWallet = (HKDWalletModel) toWallet;
        switch (fromWallet.getAssetType()) {
            case FIAT_USD -> {
                hkdWallet.setBalance(hkdWallet.getBalance().add(FiatCurrencyRateConstants.USD_TO_HKD.multiply(txnAmount)));
                break;
            }
            case CRYPTO -> {
                CryptoWalletModel cryptoWallet = (CryptoWalletModel) fromWallet;
                hkdWallet.setBalance(hkdWallet.getBalance().add(cryptoWallet.getCryptoData().get(symbol).getCryptoType().getHkdRate().multiply(txnAmount)));
                break;
            }
            case STOCK -> {
                StockWalletModel stockWallet = (StockWalletModel) fromWallet;
                hkdWallet.setBalance(hkdWallet.getBalance().add(stockWallet.getStockData().get(symbol).getStockSymbol().getHkdRate().multiply(txnAmount)));
                break;
            }
            case FUND -> {
                FundWalletModel fundWallet = (FundWalletModel) fromWallet;
                hkdWallet.setBalance(hkdWallet.getBalance().add(fundWallet.getFundData().get(symbol).getFundType().getHkdRate().multiply(txnAmount)));
                break;
            }
        }
    }
}
