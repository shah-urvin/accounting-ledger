package com.cg.account.command.model;

import com.cg.account.constants.StockSymbol;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class StockDataModel {
    private StockSymbol stockSymbol;
    private BigDecimal balanceQty;
}
