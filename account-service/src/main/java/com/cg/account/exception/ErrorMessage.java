package com.cg.account.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorMessage {
    private String errorMessage;
    private LocalDateTime timestamp;
}
