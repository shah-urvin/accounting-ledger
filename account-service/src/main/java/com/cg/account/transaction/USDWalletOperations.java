package com.cg.account.transaction;

import com.cg.account.constants.FiatCurrencyRateConstants;
import com.cg.account.entity.*;
import com.cg.account.exception.InsufficientBalanceException;
import com.cg.account.posting.dto.WalletChangeDTO;
import com.cg.account.posting.entity.Posting;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class USDWalletOperations implements WalletOperations{

    @Override
    public WalletChangeDTO debit(Wallet wallet, BigDecimal txnAmount) {
        USDWallet usdWallet = (USDWallet) wallet;
        if (usdWallet.getBalance().compareTo(txnAmount) >=0) {
            return WalletChangeDTO.builder()
                    .accountId(usdWallet.getAccount().getAccountId())
                    .walletId(usdWallet.getWalletId())
                    .assetType(usdWallet.getAssetType())
                    .newWalletBalance((usdWallet.getBalance().subtract(txnAmount)))
                    .timestamp(LocalDateTime.now()).build();
        }
        throw new InsufficientBalanceException(usdWallet.getWalletId());
    }

    @Override
    public WalletChangeDTO credit(Posting posting) {
        USDWallet usddWallet = (USDWallet) posting.getToWallet();
        BigDecimal usdValue = new BigDecimal(0.0);
        switch (posting.getFromWallet().getAssetType()) {
            case FIAT_HKD -> {
                usdValue = (FiatCurrencyRateConstants.HKD_TO_USD.multiply(posting.getTxnAmount()));
                break;
            }
            case CRYPTO -> {
                CryptoWallet fromWallet = (CryptoWallet) posting.getFromWallet();
                usdValue = fromWallet.getCryptoType().getUsdRate().multiply(posting.getTxnAmount());
                break;
            }
            case STOCK -> {
                StockWallet stockWallet = (StockWallet) posting.getFromWallet();
                usdValue = stockWallet.getStockSymbol().getUsdRate().multiply(posting.getTxnAmount());
                break;
            }
            case FUND -> {
                FundWallet fundWallet = (FundWallet) posting.getFromWallet();
                usdValue = fundWallet.getFundName().getUsdRate().multiply(posting.getTxnAmount());
                break;
            }
        }
        return WalletChangeDTO.builder()
                .accountId(usddWallet.getAccount().getAccountId())
                .walletId(usddWallet.getWalletId())
                .assetType(usddWallet.getAssetType())
                .newWalletBalance((usddWallet.getBalance().add(usdValue)))
                .timestamp(LocalDateTime.now()).build();
    }


}
