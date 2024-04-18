package com.cg.account.constants;

import java.math.BigDecimal;

public enum FundType {
    HSBC_SMALL_CAP(new BigDecimal(0.0),new BigDecimal(78.3),new BigDecimal(10.0)),
    AIA_MID_CAP(new BigDecimal(0.0),new BigDecimal(156.6),new BigDecimal(20.0)),
    CITI_LARGE_CAP(new BigDecimal(0.0),new BigDecimal(783.0),new BigDecimal(100.0));

    private BigDecimal initialBalance;
    private BigDecimal hkdRate;
    private BigDecimal usdRate;

    FundType(BigDecimal initialBalance, BigDecimal hkdRate, BigDecimal usdRate) {
        this.initialBalance = initialBalance;
        this.hkdRate = hkdRate;
        this.usdRate = usdRate;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }
}
