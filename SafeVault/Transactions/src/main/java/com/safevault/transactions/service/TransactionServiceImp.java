package com.safevault.transactions.service;

import com.safevault.transactions.dto.AccountDto;
import com.safevault.transactions.dto.TransactionDto;
import com.safevault.transactions.dto.TransferRequest;
import com.safevault.transactions.feignclient.AccountsFeignClient;
import com.safevault.transactions.model.Transaction;
import com.safevault.transactions.model.TransactionStatus;
import com.safevault.transactions.model.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImp implements TransactionService {

    @Autowired
    AccountsFeignClient accountsClient;

    @Autowired
    TransactionDtoMapper dtoMapper;

    @Override
    public ResponseEntity<?> initiateTransaction(TransferRequest request) {
        Transaction transaction = new Transaction(
                request.accountFrom(),
                request.accountTo(),
                request.amount(),
                TransactionType.TRANSFER,
                TransactionStatus.PENDING
        );
        try {
            TransactionType type = request.transactionType();
            accountsClient.transfer(request);
            transaction.setStatus(TransactionStatus.SUCCEEDED);
            TransactionDto transactionDto = dtoMapper.apply(transaction);

            return new ResponseEntity<>(transactionDto, HttpStatus.OK);

        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);

            TransactionDto transactionDto = dtoMapper.apply(transaction);

            return new ResponseEntity<>(transactionDto, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<?> getAccount(Long id) {
        Object body = accountsClient.getAccountById(id);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}
