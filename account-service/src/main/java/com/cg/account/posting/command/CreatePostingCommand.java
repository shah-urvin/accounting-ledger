package com.cg.account.posting.command;

import com.cg.account.posting.constant.PostingStatus;
import jakarta.persistence.Column;
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
    private BigDecimal txnAmount;
    private PostingStatus postingStatus;
    private LocalDateTime postingTime;
}
