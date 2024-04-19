package com.cg.account.posting.command;

import com.cg.account.constants.PostingStatus;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class RevertCreatedPostingCommand {
    @TargetAggregateIdentifier
    private String postingId;
    private String accountId;
    private PostingStatus postingStatus;
    private String reason;
}
