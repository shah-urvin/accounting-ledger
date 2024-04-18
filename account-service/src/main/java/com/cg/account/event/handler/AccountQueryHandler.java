package com.cg.account.event.handler;

import com.cg.account.dto.AccountDTO;
import com.cg.account.query.FindAccountsQuery;
import com.cg.account.repository.AccountRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountQueryHandler {

    private final AccountRepository accountRepository;

    public AccountQueryHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @QueryHandler
    public List<AccountDTO> findAccounts(FindAccountsQuery query) {
        List<AccountDTO> lstAccounts = accountRepository.findAll().stream().map(account -> AccountDTO.builder()
                    .accountId(account.getAccountId())
                    .accountStatus(account.getStatus()).build()).collect(Collectors.toList());
        return lstAccounts;
    }

}
