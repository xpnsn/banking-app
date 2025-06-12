package com.safevault.accounts.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException() {
        super("User does not exist with username or mobile number");
    }
}
