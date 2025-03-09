package com.safevault.transactions.service;

import com.safevault.transactions.dto.TransactionDto;
import com.safevault.transactions.dto.TransactionRequest;
import com.safevault.transactions.dto.accounts.CreditDebitRequest;
import com.safevault.transactions.dto.accounts.TransferRequest;
import com.safevault.transactions.feignclient.AccountsFeignClient;
import com.safevault.transactions.model.Transaction;
import com.safevault.transactions.model.TransactionStatus;
import com.safevault.transactions.model.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

@Service
public class TransactionServiceImp implements TransactionService {

    @Autowired
    AccountsFeignClient accountsClient;

    @Autowired
    TransactionDtoMapper dtoMapper;

    @Override
    public ResponseEntity<?> initiateTransaction(TransactionRequest request, String userId) {
        Transaction transaction = new Transaction(
                Long.valueOf(userId),
                request.accountTo(),
                request.amount(),
                request.transactionType(),
                TransactionStatus.PENDING
        );
        try {
            TransactionType type = request.transactionType();

            switch (type) {
                case DEPOSIT:
                    CreditDebitRequest creditRequest = new CreditDebitRequest(
                            Long.valueOf(userId),
                            request.pin(),
                            request.amount()
                    );
                    accountsClient.creditAccount(creditRequest);
                    break;
                case WITHDRAW:
                    CreditDebitRequest debitRequest = new CreditDebitRequest(
                            Long.valueOf(userId),
                            request.pin(),
                            request.amount()
                    );
                    accountsClient.debitAccount(debitRequest);
                    break;
                case TRANSFER:
                    TransferRequest transferRequest = new TransferRequest(
                            Long.valueOf(userId),
                            request.accountTo(),
                            request.pin(),
                            request.amount(),
                            request.transactionType()
                    );
                    accountsClient.transfer(transferRequest);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid transaction type: " + type);
            }


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
        return accountsClient.getAccountById(id);
    }

}
