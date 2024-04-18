package com.cg.account.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String accountId) {
        super("Account with ID " + accountId + " not found.");
    }
}
