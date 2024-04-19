package com.cg.account.event;

import com.cg.account.constants.AccountStatus;
import com.cg.account.constants.AssetType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountStatusChangedEvent {
    private String accountId;
    private AccountStatus status;
}
