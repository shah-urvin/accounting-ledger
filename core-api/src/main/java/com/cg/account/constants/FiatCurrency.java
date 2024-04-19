package com.cg.account.constants;

import java.math.BigDecimal;

public enum FiatCurrency {
    HKD(new BigDecimal(1000.0)),
    USD(new BigDecimal(1000.0));

    private BigDecimal initialBalance;

    FiatCurrency(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public BigDecimal getInitialBalance() {
        return this.initialBalance;
    }

}
