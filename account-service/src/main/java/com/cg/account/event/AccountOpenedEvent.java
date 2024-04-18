package com.cg.account.event;

import com.cg.account.command.model.WalletModel;
import com.cg.account.constants.AccountStatus;
import com.cg.account.constants.AssetType;
import com.cg.account.entity.Wallet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class AccountOpenedEvent {
    private String accountId;
    private AccountStatus status;
    private Map<AssetType, WalletModel> wallets = new HashMap<>();
}
