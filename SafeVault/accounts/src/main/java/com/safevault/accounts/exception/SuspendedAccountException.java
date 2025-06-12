package com.safevault.accounts.exception;

public class SuspendedAccountException extends RuntimeException {
    public SuspendedAccountException() {
        super("Your account has been suspended. Please reach out to our nearest branch!");
    }
}