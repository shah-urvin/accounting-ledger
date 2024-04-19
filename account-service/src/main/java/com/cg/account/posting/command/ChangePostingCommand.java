package com.cg.account.posting.command;

import com.cg.account.posting.constant.PostingStatus;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class ChangePostingCommand {
    @TargetAggregateIdentifier
    private String postingId;
    private String accountId;
    private String fromSymbol;
    private String toSymbol;
    private PostingStatus postingStatus;
}
