package com.cg.account.exception;

public class PostingNotFoundException extends RuntimeException {
    public PostingNotFoundException(String postingId) {
        super("Posting with ID " + postingId + " not found.");
    }
}
