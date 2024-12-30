package com.safevault.accounts.exception;

public class IncorrectPinException extends RuntimeException {
    public IncorrectPinException() {
        super("The pin entered is incorrect");
    }
}
