package com.cg.account.event;

import com.cg.account.command.model.WalletModel;
import com.cg.account.constants.AccountStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class AccountUpdateProcessedEvent {
    private String accountId;
    private AccountStatus accountStatus;
    private String postingId;
    private String fromWalletId;
    private String toWalletId;
    private String fromSymbol;
    private String toSymbol;
    private BigDecimal txnAmount;
    private Map<String, WalletModel> wallets = new HashMap<>();
}
