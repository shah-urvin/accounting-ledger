package com.cg.account.posting.event;

import com.cg.account.constants.PostingStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PostingCreatedEvent {
    private String postingId;
    private String accountId;
    private String fromWalletId;
    private String toWalletId;
    private String fromSymbol;
    private String toSymbol;
    private BigDecimal txnAmount;
    private PostingStatus postingStatus;
    private LocalDateTime postingTime;
}
