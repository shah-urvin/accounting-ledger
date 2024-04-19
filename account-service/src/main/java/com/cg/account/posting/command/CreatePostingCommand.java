package com.cg.account.posting.command;

import com.cg.account.posting.constant.PostingStatus;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CreatePostingCommand {

    @TargetAggregateIdentifier
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
