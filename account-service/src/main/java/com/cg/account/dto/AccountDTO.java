package com.cg.account.dto;

import com.cg.account.constants.AccountStatus;
import com.cg.account.command.model.WalletModel;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AccountDTO {
    private String accountId;
    @NotNull(message = "accountStatus is a mandatory field.")
    private AccountStatus accountStatus;
    private Map<String, WalletModel> wallets;
}
