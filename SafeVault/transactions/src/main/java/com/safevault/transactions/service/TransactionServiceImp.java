package com.safevault.transactions.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safevault.transactions.dto.*;
import com.safevault.transactions.dto.accounts.AccountDto;
import com.safevault.transactions.dto.accounts.CreditDebitRequest;
import com.safevault.transactions.dto.accounts.TransferRequest;
import com.safevault.transactions.feignclient.AccountsFeignClient;
import com.safevault.transactions.feignclient.AuthenticationFeignClient;
import com.safevault.transactions.feignclient.NotificationClient;
import com.safevault.transactions.model.Transaction;
import com.safevault.transactions.model.TransactionStatus;
import com.safevault.transactions.model.TransactionType;
import com.safevault.transactions.repository.TransactionRepository;
import feign.FeignException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class TransactionServiceImp implements TransactionService {

    private final AuthenticationFeignClient authClient;
    private final AccountsFeignClient accountsClient;
    private final TransactionDtoMapper dtoMapper;
    private final ObjectMapper objectMapper;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImp(AuthenticationFeignClient authClient, AccountsFeignClient accountsClient, TransactionDtoMapper dtoMapper, ObjectMapper objectMapper, TransactionRepository transactionRepository) {
        this.authClient = authClient;
        this.accountsClient = accountsClient;
        this.dtoMapper = dtoMapper;
        this.objectMapper = objectMapper;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public ResponseEntity<?> initiateTransaction(TransactionRequest request) {

        UserDto userDto = objectMapper.convertValue(authClient.validate().getBody(), UserDto.class);

        if(!userDto.accounts().contains(request.accountFrom())) {
            return new ResponseEntity<>("Invalid account", HttpStatus.BAD_REQUEST);
        }
        Transaction transaction = new Transaction(
                request.accountFrom(),
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
                            request.accountTo(),
                            request.pin(),
                            request.amount()
                    );
                    accountsClient.creditAccount(creditRequest);
                    break;
                case WITHDRAW:
                    CreditDebitRequest debitRequest = new CreditDebitRequest(
                            request.accountFrom(),
                            request.pin(),
                            request.amount()
                    );
                    accountsClient.debitAccount(debitRequest);
                    break;
                case TRANSFER:
                    TransferRequest transferRequest = new TransferRequest(
                            request.accountFrom(),
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
            transaction = transactionRepository.save(transaction);
            TransactionDto transactionDto = dtoMapper.apply(transaction);
            accountsClient.addTransaction(transactionDto);
            return new ResponseEntity<>(transactionDto, HttpStatus.OK);

        } catch (FeignException.BadRequest e) {
            return new ResponseEntity<>(e.contentUTF8(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> getAccount(Long id) {
        return accountsClient.getAccountById(id);
    }

    @Override
    public ResponseEntity<?> getTransactions(Long id, String type, Pageable pageable) {

        UserDto userDto = objectMapper.convertValue(authClient.validate().getBody(), UserDto.class);
        if(!userDto.accounts().contains(id)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Page<Transaction> transactionPage;
        if(type.equalsIgnoreCase("all")) {
            transactionPage = transactionRepository.findByFromAccountIdOrToAccountId(id, id, pageable);
        } else if(type.equalsIgnoreCase("credit")) {
            transactionPage = transactionRepository.findByToAccountId(id, pageable);
        } else if(type.equalsIgnoreCase("debit")) {
            transactionPage = transactionRepository.findByFromAccountId(id, pageable);
        } else throw new IllegalArgumentException("Invalid transaction type: " + type);
        return new ResponseEntity<>(new PagedModel<>(transactionPage), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getTransactionFilter(TransactionFilterRequest request, Pageable pageable) {
        UserDto userDto = objectMapper.convertValue(authClient.validate().getBody(), UserDto.class);
        if(!userDto.accounts().contains(Long.valueOf(request.id()))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Page<Transaction> transactions = transactionRepository.findByFromAccountIdAndTimeStampBetweenOrToAccountIdAndTimeStampBetween(
                Long.valueOf(request.id()),
                request.startDate(),
                request.endDate(),
                Long.valueOf(request.id()),
                request.startDate(),
                request.endDate(),
                pageable
        );
        return new ResponseEntity<>(new PagedModel<>(transactions), HttpStatus.OK);
    }
}
