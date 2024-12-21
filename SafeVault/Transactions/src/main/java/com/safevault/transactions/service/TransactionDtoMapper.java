package com.safevault.transactions.service;

import com.safevault.transactions.dto.AccountDto;
import com.safevault.transactions.dto.TransactionDto;
import com.safevault.transactions.feignclient.AccountsFeignClient;
import com.safevault.transactions.model.Transaction;
import com.safevault.transactions.model.TransactionStatus;
import com.safevault.transactions.model.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class TransactionDtoMapper implements Function<Transaction, TransactionDto> {

    @Autowired
    AccountsFeignClient accountsClient;
    @Override
    public TransactionDto apply(Transaction transaction) {
        AccountDto accountFrom = (AccountDto) accountsClient.getAccountById(transaction.getFromAccountId());
        AccountDto accountTo = (AccountDto) accountsClient.getAccountById(transaction.getToAccountId());
        return new TransactionDto(
                accountFrom.accountHolderName(),
                accountTo.accountHolderName(),
                transaction.getAmount(),
                transaction.getTimeStamp(),
                transaction.getTransactionType(),
                transaction.getStatus()
        );
    }
}
