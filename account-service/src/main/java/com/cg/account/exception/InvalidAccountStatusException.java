package com.cg.account.exception;

import com.cg.account.constants.AccountStatus;

public class InvalidAccountStatusException extends RuntimeException{
    public InvalidAccountStatusException(AccountStatus accountStatus) {
        super("Account Status " + accountStatus + " not found.");
    }
}
