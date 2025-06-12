package com.safevault.transactions.exceptions;

public class InvalidTransactionType extends RuntimeException {
    public InvalidTransactionType(String type) {
        super("Invalid transaction type: " + type);
    }
}
