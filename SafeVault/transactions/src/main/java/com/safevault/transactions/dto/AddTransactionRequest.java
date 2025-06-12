package com.safevault.transactions.dto;

public record AddTransactionRequest(
        Long accountId,
        Long TransactionId
) {
}
