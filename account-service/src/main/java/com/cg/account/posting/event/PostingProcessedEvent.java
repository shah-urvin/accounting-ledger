package com.cg.account.posting.event;

import com.cg.account.constants.AssetType;
import com.cg.account.constants.CryptoType;
import com.cg.account.constants.FundType;
import com.cg.account.constants.StockSymbol;
import com.cg.account.posting.constant.PostingStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PostingProcessedEvent {
    private String postingId;
    private String accountId;
    private PostingStatus postingStatus;
    private String walletId;
    private BigDecimal newWalletBalance;
    private AssetType assetType;
    private StockSymbol stockSymbol;
    private FundType fundType;
    private CryptoType cryptoType;
    private LocalDateTime timestamp;
}
