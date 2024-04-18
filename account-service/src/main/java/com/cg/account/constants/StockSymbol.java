package com.cg.account.constants;

import java.math.BigDecimal;

public enum StockSymbol {
    CAPGEMINI(0l,new BigDecimal(1710.0),new BigDecimal(210.0)),
    HSBC(0l,new BigDecimal(65.0),new BigDecimal(10.0)),
    CITI(0l,new BigDecimal(450.0),new BigDecimal(60.0));

    private Long initialBalance;
    private BigDecimal hkdRate;
    private BigDecimal usdRate;

    StockSymbol(Long initialBalance, BigDecimal hkdRate, BigDecimal usdRate) {
        this.initialBalance = initialBalance;
        this.hkdRate = hkdRate;
        this.usdRate = usdRate;
    }

    public Long getInitialBalance() {
        return initialBalance;
    }

    public BigDecimal getHkdRate() {
        return hkdRate;
    }

    public BigDecimal getUsdRate() {
        return usdRate;
    }
}
