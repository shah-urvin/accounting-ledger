package com.cg.account.exception;

import com.cg.account.constants.AccountStatus;

public class InoperativeAccountException extends RuntimeException{
    public InoperativeAccountException(String accountId, AccountStatus accountStatus) {
        super("The account:"+accountId+" is in "+accountStatus.name()+" thus no posting allowed");
    }
}
