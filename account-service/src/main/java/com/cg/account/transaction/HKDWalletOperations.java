package com.cg.account.transaction;

import com.cg.account.constants.FiatCurrencyRateConstants;
import com.cg.account.entity.*;
import com.cg.account.exception.InsufficientBalanceException;
import com.cg.account.posting.dto.WalletChangeDTO;
import com.cg.account.posting.entity.Posting;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HKDWalletOperations implements WalletOperations {

    @Override
    public WalletChangeDTO debit(Wallet wallet, BigDecimal txnAmount) {
        HKDWallet hkdWallet = (HKDWallet) wallet;
        if (hkdWallet.getBalance().compareTo(txnAmount) >=0) {
            return WalletChangeDTO.builder()
                    .accountId(hkdWallet.getAccount().getAccountId())
                    .walletId(hkdWallet.getWalletId())
                    .assetType(hkdWallet.getAssetType())
                    .newWalletBalance((hkdWallet.getBalance().subtract(txnAmount)))
                    .timestamp(LocalDateTime.now()).build();
        }
        throw new InsufficientBalanceException(hkdWallet.getWalletId());

    }

    @Override
    public WalletChangeDTO credit(Posting posting) {
        HKDWallet hkdWallet = (HKDWallet) posting.getToWallet();
        BigDecimal hkdValue = new BigDecimal(0.0);
        switch (posting.getFromWallet().getAssetType()) {
            case FIAT_USD -> {
                hkdValue = (FiatCurrencyRateConstants.USD_TO_HKD.multiply(posting.getTxnAmount()));
                break;
            }
            case CRYPTO -> {
                CryptoWallet fromWallet = (CryptoWallet) posting.getFromWallet();
                hkdValue = fromWallet.getCryptoType().getHkdRate().multiply(posting.getTxnAmount());
                break;
            }
            case STOCK -> {
                StockWallet stockWallet = (StockWallet) posting.getFromWallet();
                hkdValue = stockWallet.getStockSymbol().getHkdRate().multiply(posting.getTxnAmount());
                break;
            }
            case FUND -> {
                FundWallet fundWallet = (FundWallet) posting.getFromWallet();
                hkdValue = fundWallet.getFundName().getHkdRate().multiply(posting.getTxnAmount());
                break;
            }
        }
        return WalletChangeDTO.builder()
                .accountId(hkdWallet.getAccount().getAccountId())
                .walletId(hkdWallet.getWalletId())
                .assetType(hkdWallet.getAssetType())
                .newWalletBalance((hkdWallet.getBalance().add(hkdValue)))
                .timestamp(LocalDateTime.now()).build();
    }


}
