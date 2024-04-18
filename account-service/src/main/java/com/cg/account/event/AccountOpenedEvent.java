package com.cg.account.event;

import com.cg.account.constants.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class AccountOpenedEvent {
    private String accountId;
    private AccountStatus status;
    private List<String> wallets = new ArrayList<>();
}
