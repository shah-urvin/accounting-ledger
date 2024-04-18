package com.cg.account.command;

import com.cg.account.constants.AssetType;
import com.cg.account.constants.CryptoType;
import com.cg.account.constants.FundType;
import com.cg.account.constants.StockSymbol;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ChangeWalletBalanceCommand {
    @TargetAggregateIdentifier
    private String accountId;
    private String walletId;
    private BigDecimal newWalletBalance;
    private AssetType assetType;
    private StockSymbol stockSymbol;
    private FundType fundType;
    private CryptoType cryptoType;
    private LocalDateTime timestamp;
}
