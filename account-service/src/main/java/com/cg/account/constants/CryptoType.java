package com.cg.account.constants;

import java.math.BigDecimal;

/**
 * CryptoType is an Enum which contains Limited Crypto Type BTC, ETH, SHIB
 * It provides the initialBalance, HKD Rate, USD Rate : Note at this time I have considered same rate for the Buy and Sale
 */
public enum CryptoType {
    BTC(new BigDecimal(0.0),new BigDecimal(100000.0),new BigDecimal(12000.0)),
    ETH(new BigDecimal(0.0),new BigDecimal(10000.0),new BigDecimal(120.0)),
    SHIB(new BigDecimal(0.0),new BigDecimal(10.0),new BigDecimal(0.15));

    private BigDecimal initialBalance;
    private BigDecimal hkdRate;
    private BigDecimal usdRate;

    CryptoType(BigDecimal initialBalance,BigDecimal hkdRate,BigDecimal usdRate) {
        this.initialBalance = initialBalance;
    }

    public BigDecimal getInitalBalance() {
        return initialBalance;
    }

    public BigDecimal getHkdRate() {
        return hkdRate;
    }

    public BigDecimal getUsdRate() {
        return usdRate;
    }
}
