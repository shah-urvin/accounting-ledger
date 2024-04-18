package com.cg.account.exception;

public class WalletNotFoundException extends RuntimeException{
    public WalletNotFoundException(String walletId,String accountId) {
        super("Wallet " + walletId + " with Account" +accountId+" not found.");
    }
}
