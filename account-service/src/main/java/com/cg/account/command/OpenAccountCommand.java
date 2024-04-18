package com.cg.account.command;

import com.cg.account.constants.AccountStatus;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class OpenAccountCommand {
    @TargetAggregateIdentifier
    private final String accountId;
    private final AccountStatus accountStatus;
}
