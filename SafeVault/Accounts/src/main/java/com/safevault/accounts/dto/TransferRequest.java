package com.safevault.accounts.dto;

public record TransferRequest(
        Long accountFrom,
        Long accountTo,
        Double amount
) {}
