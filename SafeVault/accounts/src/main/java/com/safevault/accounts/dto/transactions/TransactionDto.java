package com.safevault.accounts.dto.transactions;

import java.time.LocalDateTime;

public record TransactionDto(
    Long id,
    String accountTo,
    String accountFrom,
    Double amount,
    LocalDateTime timeStamp,
    TransactionType transactionType,
    TransactionStatus transactionStatus
) {}