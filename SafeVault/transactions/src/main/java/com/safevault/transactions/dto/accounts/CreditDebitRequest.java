package com.safevault.transactions.dto.accounts;

public record CreditDebitRequest(
        Long accountId,
        String pin,
        Double amount
) {}
