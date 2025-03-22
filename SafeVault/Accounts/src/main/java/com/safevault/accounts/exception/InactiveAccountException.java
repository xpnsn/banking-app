package com.safevault.accounts.exception;

public class InactiveAccountException extends RuntimeException {
    public InactiveAccountException() {
        super("Your Account is Inactive. Please activate your account from the nearest branch!");
    }
}