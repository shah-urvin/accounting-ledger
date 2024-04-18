package com.cg.account.repository;

import com.cg.account.entity.AccountLookup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountLookupRepository extends JpaRepository<AccountLookup,String> {
    List<AccountLookup> findByAccountId(String accountId);
    AccountLookup findByAccountIdAndWalletId(String accountId,String walletId);
}
