package com.safevault.notifications.dto.transations;

import java.time.LocalDateTime;

public record TransactionDto(Long id,
     String accountTo,
     String accountFrom,
     Double amount,
     LocalDateTime timeStamp,
     TransactionType transactionType,
     TransactionStatus transactionStatus
) {}
