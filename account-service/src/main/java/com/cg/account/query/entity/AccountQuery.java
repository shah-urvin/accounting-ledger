package com.cg.account.query.entity;

import com.cg.account.constants.AccountStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@Table(name = "account_query")
public class AccountQuery {
    @Id
    @Column(name = "account_id")
    private String accountId;

    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QueryWallet> wallets = new ArrayList<>();
}
