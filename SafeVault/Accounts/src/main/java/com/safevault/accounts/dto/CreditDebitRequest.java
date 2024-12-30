package com.safevault.accounts.dto;

public record CreditDebitRequest(
        Long accountId,
        String pin,
        Double amount
) {}
