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

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "posting_status")
    @Enumerated(EnumType.STRING)
    private PostingStatus postingStatus;

    @ManyToOne
    @JoinColumn(name = "from_wallet_id")
    private Wallet fromWallet;

    @ManyToOne
    @JoinColumn(name = "to_wallet_id")
    private Wallet toWallet;

    @Column(name = "txn_amount")
    private BigDecimal txnAmount;

    @Column(name = "posting_time")
    private LocalDateTime postingTime;
}
