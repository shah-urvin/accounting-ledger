package com.cg.account.exception;

public class InvalidPostingSymbolException extends RuntimeException {

    public InvalidPostingSymbolException(String symbol){
        super("Symbol "+symbol+" not allowed for the posting");
    }
}
