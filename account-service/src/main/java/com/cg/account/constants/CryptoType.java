package com.cg.account.constants;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * CryptoType is an Enum which contains Limited Crypto Type BTC, ETH, SHIB
 * It provides the initialBalance, HKD Rate, USD Rate : Note at this time I have considered same rate for the Buy and Sale
 */
public enum CryptoType {
    BTC("BTC",new BigDecimal(0.0),new BigDecimal(100000.0),new BigDecimal(12000.0)),
    ETH("ETH",new BigDecimal(0.0),new BigDecimal(10000.0),new BigDecimal(120.0)),
    SHIB("SHIB",new BigDecimal(0.0),new BigDecimal(10.0),new BigDecimal(0.15));

    private String symbol;
    private BigDecimal initialBalance;
    private BigDecimal hkdRate;
    private BigDecimal usdRate;

    CryptoType(String symbol,BigDecimal initialBalance,BigDecimal hkdRate,BigDecimal usdRate) {
        this.initialBalance = initialBalance;
        this.symbol = symbol;
        this.hkdRate = hkdRate;
        this.usdRate = usdRate;
    }

    public BigDecimal getHkdRate() {
        return hkdRate;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }


    public BigDecimal getUsdRate() {
        return usdRate;
    }

    public static CryptoType getCryptoTypeBySymbol(String symbol) {
        return Arrays.stream(CryptoType.values()).filter(cryptoType -> cryptoType.getSymbol().equals(symbol)).findFirst().get();
    }

}
