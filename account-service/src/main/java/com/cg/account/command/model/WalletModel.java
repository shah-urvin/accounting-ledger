package com.cg.account.command.model;

import com.cg.account.constants.AssetType;
import com.cg.account.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class WalletModel {
    private String walletId;
    private String accountId;
    private AssetType assetType;
}
