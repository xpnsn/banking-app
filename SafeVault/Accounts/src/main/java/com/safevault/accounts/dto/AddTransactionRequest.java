package com.safevault.accounts.dto;

public record AddTransactionRequest(
        Long accountId,
        Long TransactionId
) {
}
