package com.cg.account.event;

import com.cg.account.command.model.WalletModel;
import com.cg.account.constants.AccountStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountUpdateProcessedEvent {
    private String accountId;
    private AccountStatus accountStatus;
    private String postingId;
    private String fromWalletId;
    private String toWalletId;
    private BigDecimal txnAmount;
}
