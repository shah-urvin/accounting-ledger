package com.cg.account.command;

import com.cg.account.constants.AccountStatus;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

@Data
@Builder
public class ProcessUpdateAccountCommand {
    @TargetAggregateIdentifier
    private String accountId;
    private AccountStatus accountStatus;
    private String postingId;
    private String fromWalletId;
    private String toWalletId;
    private BigDecimal txnAmount;
}
