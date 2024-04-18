package com.cg.account.posting.dto;

import com.cg.account.posting.constant.PostingStatus;
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
    private PostingStatus postingStatus;
    private BigDecimal txnAmount;
    private LocalDateTime postingTime;
}
