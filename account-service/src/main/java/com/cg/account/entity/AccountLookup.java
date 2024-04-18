package com.cg.account.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class AccountLookup {
    @Id
    private String walletId;
    private String accountId;
}
