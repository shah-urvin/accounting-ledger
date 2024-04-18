package com.cg.account.posting.dto;

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
    private String toWalletId;
    private BigDecimal txnAmount;
    private LocalDateTime postingTime;
}
