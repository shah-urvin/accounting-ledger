package com.cg.account.aggregate;

import com.cg.account.command.ChangeAccountStatusCommand;
import com.cg.account.command.OpenAccountCommand;
import com.cg.account.constants.AccountStatus;
import com.cg.account.constants.AssetType;
import com.cg.account.event.AccountOpenedEvent;
import com.cg.account.event.AccountStatusChangedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.common.StringUtils;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class AccountAggregate {

    @AggregateIdentifier
    private String accountId;
    private AccountStatus accountStatus;
    private Map<AssetType,String> wallets;

    public AccountAggregate() {}

    @CommandHandler
    public AccountAggregate(OpenAccountCommand openAccountCommand) {
        // Validation the OpenAccountCommand
        if(StringUtils.emptyOrNull(openAccountCommand.getAccountId())) {
            throw new IllegalArgumentException("AccountId can not be null..");
        }

        apply(AccountOpenedEvent.builder()
        .wallets(Arrays.stream(AssetType.values()).collect(Collectors.toMap(assetType -> assetType,assetType->UUID.randomUUID().toString())))
                .accountId(openAccountCommand.getAccountId())
                .status(AccountStatus.OPEN).build());
    }

    @EventSourcingHandler
    public void on(AccountOpenedEvent accountOpenedEvent) {
        System.out.println("AccountOpenedEvent get called...."+accountOpenedEvent.getAccountId());
        this.accountId=accountOpenedEvent.getAccountId();
        this.accountStatus=accountOpenedEvent.getStatus();
        this.wallets = accountOpenedEvent.getWallets();
    }

    @CommandHandler
    public void handle(ChangeAccountStatusCommand changeAccountStatusCommand) {
        System.out.println("ChangeAccountStatusCommand get called..."+changeAccountStatusCommand.getAccountId());
        if(StringUtils.emptyOrNull(changeAccountStatusCommand.getAccountId())) {
            throw new IllegalArgumentException("AccountId can not be null..");
        }

        if(ObjectUtils.isEmpty(changeAccountStatusCommand.getStatus())) {
            throw new IllegalArgumentException("Account Status can not be null.");
        }
        this.accountId = changeAccountStatusCommand.getAccountId();
        this.accountStatus = changeAccountStatusCommand.getStatus();
        apply(AccountStatusChangedEvent.builder().accountId(changeAccountStatusCommand.getAccountId()).status(changeAccountStatusCommand.getStatus()).build());
    }

    @EventSourcingHandler
    public void on(AccountStatusChangedEvent accountStatusChangedEvent) {
        System.out.println("AccountStatusChangedEvent got called from AccountAggregate...");
        this.accountId=accountStatusChangedEvent.getAccountId();
        this.accountStatus=accountStatusChangedEvent.getStatus();
    }


}
