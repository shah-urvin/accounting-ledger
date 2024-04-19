package com.cg.account.posting.event;


import com.cg.account.constants.PostingStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PostingCreatedRevertedEvent {
    private String postingId;
    private String accountId;
    private PostingStatus postingStatus;
}
