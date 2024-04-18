package com.cg.account.query.rest;

import com.cg.account.dto.AccountDTO;
import com.cg.account.query.FindAccountsQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/accounts")
public class AccountQueryController {

    @Autowired
    QueryGateway queryGateway;

    @GetMapping
    public List<AccountDTO> getAccounts() {
        FindAccountsQuery findAccountsQuery = new FindAccountsQuery();
        List<AccountDTO> accounts = queryGateway.query(findAccountsQuery,
                ResponseTypes.multipleInstancesOf(AccountDTO.class)).join();
        return accounts;
    }
}
