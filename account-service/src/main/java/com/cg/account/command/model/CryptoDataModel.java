package com.cg.account.command.model;

import com.cg.account.constants.CryptoType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CryptoDataModel {
    private CryptoType cryptoType;
    private BigDecimal balance;
}
