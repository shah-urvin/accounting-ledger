package com.cg.account.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


// At the controller we can use @Valid to validate this values against the RequestBody of the PostingDTO
@Data
@Builder
public class PostingDTO {
    private String accountId;
    private String postingId;
    @NotBlank(message = "FromWalletId is a required field.")
    private String fromWalletId;
    @NotBlank(message = "toWalletId is a required field.")
    private String toWalletId;
    private String fromSymbol;
    private String toSymbol;
    @Min(value = 0,message = "Transaction amount must be greater than 0.")
    private BigDecimal txnAmount;
    private LocalDate txnTime;
}
