package com.cg.account.posting.command;

import com.cg.account.posting.constant.PostingStatus;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProcessPostingCommand {
    @TargetAggregateIdentifier
    private String postingId;
    private String accountId;
    private PostingStatus postingStatus;
    private String fromWalletId;
    private String toWalletId;
    private BigDecimal txnAmount;
    private LocalDateTime postingTime;
}
