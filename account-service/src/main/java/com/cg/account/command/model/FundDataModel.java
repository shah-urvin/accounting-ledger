package com.cg.account.command.model;


import com.cg.account.constants.FundType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FundDataModel {
    private FundType fundType;
    private BigDecimal balance;
}
