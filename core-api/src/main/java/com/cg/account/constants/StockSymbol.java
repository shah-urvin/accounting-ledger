package com.cg.account.constants;

import java.math.BigDecimal;
import java.util.Arrays;

public enum StockSymbol {
    CAPGEMINI("CAPGEMINI",new BigDecimal(0.0),new BigDecimal(1710.0),new BigDecimal(210.0)),
    HSBC("HSBC",new BigDecimal(0.0),new BigDecimal(65.0),new BigDecimal(10.0)),
    CITI("CITI",new BigDecimal(0.0),new BigDecimal(450.0),new BigDecimal(60.0));

    private String symbol;
    private BigDecimal initialBalance;
    private BigDecimal hkdRate;
    private BigDecimal usdRate;

    StockSymbol(String symbol,BigDecimal initialBalance, BigDecimal hkdRate, BigDecimal usdRate) {
        this.symbol = symbol;
        this.initialBalance = initialBalance;
        this.hkdRate = hkdRate;
        this.usdRate = usdRate;
    }

    public String getSymbol() {
        return symbol;
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

    public static StockSymbol getStockSymbolBySymbol(String symbol) {
        return Arrays.stream(StockSymbol.values()).filter(stockSymbol -> stockSymbol.getSymbol().equals(symbol)).findFirst().get();
    }
}
