package com.cg.account.event;

import com.cg.account.constants.AccountStatus;
import com.cg.account.constants.AssetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class AccountOpenedEvent {
    private String accountId;
    private AccountStatus status;
    private Map<AssetType,String> wallets = new HashMap<>();
}
