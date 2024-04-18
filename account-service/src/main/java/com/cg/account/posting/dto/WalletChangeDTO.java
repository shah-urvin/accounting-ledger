package com.cg.account.posting.dto;

import com.cg.account.constants.AssetType;
import com.cg.account.constants.CryptoType;
import com.cg.account.constants.FundType;
import com.cg.account.constants.StockSymbol;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WalletChangeDTO {
    private String walletId;
    private String accountId;
    private BigDecimal newWalletBalance;
    private AssetType assetType;
    private StockSymbol stockSymbol;
    private FundType fundType;
    private CryptoType cryptoType;
    private LocalDateTime timestamp;
}
