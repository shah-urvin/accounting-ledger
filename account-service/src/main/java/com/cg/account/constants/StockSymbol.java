package com.cg.account.constants;

import java.math.BigDecimal;

public enum StockSymbol {
    CAPGEMINI(new BigDecimal(0.0),new BigDecimal(1710.0),new BigDecimal(210.0)),
    HSBC(new BigDecimal(0.0),new BigDecimal(65.0),new BigDecimal(10.0)),
    CITI(new BigDecimal(0.0),new BigDecimal(450.0),new BigDecimal(60.0));

    private BigDecimal initialBalance;
    private BigDecimal hkdRate;
    private BigDecimal usdRate;

    StockSymbol(BigDecimal initialBalance, BigDecimal hkdRate, BigDecimal usdRate) {
        this.initialBalance = initialBalance;
        this.hkdRate = hkdRate;
        this.usdRate = usdRate;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public BigDecimal getHkdRate() {
        return hkdRate;
    }

    public BigDecimal getUsdRate() {
        return usdRate;
    }
}
