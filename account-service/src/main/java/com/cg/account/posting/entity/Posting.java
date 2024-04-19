package com.cg.account.posting.entity;

import com.cg.account.entity.Account;
import com.cg.account.entity.Wallet;
import com.cg.account.posting.constant.PostingStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "posting")
public class Posting {

    @Id
    @Column(name = "posting_id")
    private String postingId;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "posting_status")
    @Enumerated(EnumType.STRING)
    private PostingStatus postingStatus;

    @Column(name = "from_wallet_id")
    private String fromWalletId;

    @Column(name = "to_wallet_id")
    private String toWalletId;

    @Column(name = "txn_amount")
    private BigDecimal txnAmount;

    @Column(name = "posting_time")
    private LocalDateTime postingTime;
}
