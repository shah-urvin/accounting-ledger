package com.cg.account.posting.event;

import com.cg.account.posting.constant.PostingStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostingChangedEvent {
    private String postingId;
    private String accountId;
    private PostingStatus postingStatus;
}