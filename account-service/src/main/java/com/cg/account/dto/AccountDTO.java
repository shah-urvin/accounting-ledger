package com.cg.account.dto;

import com.cg.account.constants.AccountStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDTO {
    private String accountId;
    @NotNull(message = "accountStatus is a mandatory field.")
    private AccountStatus accountStatus;
}
