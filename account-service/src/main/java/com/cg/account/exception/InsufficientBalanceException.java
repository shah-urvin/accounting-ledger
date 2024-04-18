package com.cg.account.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String walletId) {
        super("Insufficient Balance with wallet :"+walletId);
    }
}
