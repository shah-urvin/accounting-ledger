package com.cg.account.posting.dto;

import com.cg.account.constants.PostingStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PostingDTO {
    private String postingId;
    private String accountId;
    private String fromWalletId;
    private String fromSymbol;
    private String toSymbol;
    private String toWalletId;
    private PostingStatus postingStatus;
    private BigDecimal txnAmount;
    private LocalDateTime postingTime;
}
