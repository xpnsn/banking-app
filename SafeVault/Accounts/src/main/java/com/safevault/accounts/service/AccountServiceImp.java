package com.safevault.accounts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safevault.accounts.dto.*;
import com.safevault.accounts.exception.AccountNotFoundException;
import com.safevault.accounts.exception.IncorrectPinException;
import com.safevault.accounts.exception.InsufficientBalanceException;
import com.safevault.accounts.feignClient.SecurityFeignClient;
import com.safevault.accounts.model.Account;
import com.safevault.accounts.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service

public class AccountServiceImp implements AccountService {

    private final AccountRepository repository;
    private final AccountDtoMapper mapper;
    private final SecurityFeignClient securityClient;
    private final ObjectMapper objectMapper;

    public AccountServiceImp(AccountRepository repository, AccountDtoMapper mapper, SecurityFeignClient securityClient, ObjectMapper objectMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.securityClient = securityClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean accountExist(Long accountId) {
        return repository.findById(accountId).orElse(null) != null;
    }

    @Override
    public ResponseEntity<?> getAccountById(Long id) {
        try {
            Account account = repository.findById(id).orElseThrow(AccountNotFoundException::new);
            AccountDto accountDto = mapper.apply(account);
            return new ResponseEntity<>(accountDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @Override
    public ResponseEntity<?> addAccount(AccountCreationRequest accountCreationRequest, String userId) {
        UserDto user = objectMapper.convertValue(securityClient.validate(userId).getBody(), UserDto.class);
        Account account = new Account(
                user.name(),
                accountCreationRequest.accountType(),
                Long.valueOf(userId),
                accountCreationRequest.pin()
        );
        repository.save(account);
        AccountDto accountDto = mapper.apply(account);
        return new ResponseEntity<>(accountDto, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> removeAccount(AccountDeletionRequest accountDeletionRequest) {
//        try {
//            if(!accountExist(accountDeletionRequest.userId())) {
//                throw new AccountNotFoundException();
//            }
//            repository.deleteById(accountDeletionRequest.userId());
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } catch (RuntimeException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
        return new ResponseEntity<>("Account Removed!", HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<?> creditAccount(CreditDebitRequest request) {
        try {
            Account account = repository.findById(request.accountId()).orElseThrow(AccountNotFoundException::new);
            if(!account.getPin().equals(request.pin())) {
                throw new IncorrectPinException();
            }
            account.setBalance(account.getBalance() + request.amount());
            repository.save(account);
            AccountDto accountDto = mapper.apply(account);
            return new ResponseEntity<>(accountDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> debitAccount(CreditDebitRequest request){
        try {
            Account account = repository.findById(request.accountId()).orElseThrow(AccountNotFoundException::new);
            if(!account.getPin().equals(request.pin())) {
                throw new IncorrectPinException();
            }
            if(account.getBalance() < request.amount()) {
                throw new InsufficientBalanceException();
            }
            account.setBalance(account.getBalance() - request.amount());
            repository.save(account);
            AccountDto accountDto = mapper.apply(account);
            return new ResponseEntity<>(accountDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> transfer(TransferRequest request) {
        CreditDebitRequest creditRequest = new CreditDebitRequest(request.accountTo(), request.pin(), request.amount());
        CreditDebitRequest debitRequest = new CreditDebitRequest(request.accountFrom(), request.pin(), request.amount());
        creditAccount(creditRequest);
        return debitAccount(debitRequest);
    }

    @Override
    public ResponseEntity<?> test(String userId) {

        return new ResponseEntity<>(securityClient.validate(userId).getBody(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getUnverifiedAccounts() {
        List<Account> unverifiedAccounts = repository.findAll().stream().filter(account -> !account.isVerified()).toList();
        return new ResponseEntity<>(unverifiedAccounts, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> verifyAccount(Long accountId) {
        Account account = repository.findById(accountId).orElse(null);
        if(account == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        account.setVerified(true);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }
}
