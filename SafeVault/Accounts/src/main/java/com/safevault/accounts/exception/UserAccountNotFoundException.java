package com.safevault.accounts.exception;

public class UserAccountNotFoundException extends RuntimeException {
    public UserAccountNotFoundException() {
        super("User does not exist with username or mobile number");
    }
}
