package com.cg.account.constants;

import java.math.BigDecimal;
import java.util.Arrays;

public enum FundType {
    HSBC_SMALL_CAP("HSBC_SMALL_CAP",new BigDecimal(0.0),new BigDecimal(78.3),new BigDecimal(10.0)),
    AIA_MID_CAP("AIA_MID_CAP",new BigDecimal(0.0),new BigDecimal(156.6),new BigDecimal(20.0)),
    CITI_LARGE_CAP("CITI_LARGE_CAP",new BigDecimal(0.0),new BigDecimal(783.0),new BigDecimal(100.0));

    private String symbol;
    private BigDecimal initialBalance;
    private BigDecimal hkdRate;
    private BigDecimal usdRate;

    FundType(String symbol,BigDecimal initialBalance, BigDecimal hkdRate, BigDecimal usdRate) {
        this.symbol = symbol;
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

    public String getSymbol() {
        return symbol;
    }

    public static FundType getFundTypeBySymbol(String symbol) {
        return Arrays.stream(FundType.values()).filter(fundType -> fundType.getSymbol().equals(symbol)).findFirst().get();
    }
}
