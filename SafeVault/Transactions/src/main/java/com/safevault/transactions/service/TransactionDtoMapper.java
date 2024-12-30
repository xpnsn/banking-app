package com.safevault.transactions.service;

import com.safevault.transactions.dto.accounts.AccountDto;
import com.safevault.transactions.dto.TransactionDto;
import com.safevault.transactions.feignclient.AccountsFeignClient;
import com.safevault.transactions.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class TransactionDtoMapper implements Function<Transaction, TransactionDto> {

    @Autowired
    AccountsFeignClient accountsClient;
    @Override
    public TransactionDto apply(Transaction transaction) {
        return new TransactionDto(
                transaction.getFromAccountId().toString(),
                transaction.getToAccountId().toString(),
                transaction.getAmount(),
                transaction.getTimeStamp(),
                transaction.getTransactionType(),
                transaction.getStatus()
        );
    }
}
