package com.cg.account.posting.event;

import com.cg.account.constants.PostingStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostingChangedEvent {
    private String postingId;
    private String accountId;
    private String fromSymbol;
    private String toSymbol;
    private PostingStatus postingStatus;
}