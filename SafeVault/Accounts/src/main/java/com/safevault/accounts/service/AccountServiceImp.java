package com.safevault.accounts.service;

import com.safevault.accounts.dto.*;
import com.safevault.accounts.exception.IncorrectPinException;
import com.safevault.accounts.exception.InsufficientBalanceException;
import com.safevault.accounts.exception.UserAccountNotFoundException;
import com.safevault.accounts.model.Account;
import com.safevault.accounts.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;

@Service

public class AccountServiceImp implements AccountService {

    private final AccountRepository repository;
    private final AccountDtoMapper mapper;

    public AccountServiceImp(AccountRepository repository, AccountDtoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public boolean userExists(String username) {
        return repository.findByUsername(username) != null;
    }

    @Override
    public ResponseEntity<?> getAccountById(Long id) {
        try {
            Account account = repository.findById(id).orElseThrow(UserAccountNotFoundException::new);
            AccountDto accountDto = mapper.apply(account);
            return new ResponseEntity<>(accountDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @Override
    public ResponseEntity<?> addAccount(AccountCreationRequest accountCreationRequest) {
        if(userExists(accountCreationRequest.username())) {
            return new ResponseEntity<>("User already exist with username or mobile number", HttpStatus.BAD_REQUEST);
        }
        Account account = new Account(
                accountCreationRequest.mobileNumber(),
                accountCreationRequest.username(),
                accountCreationRequest.email(),
                accountCreationRequest.accountHolderName(),
                accountCreationRequest.accountType(),
                accountCreationRequest.password(),
                accountCreationRequest.pin()
        );
        repository.save(account);
        AccountDto accountDto = mapper.apply(account);
        return new ResponseEntity<>(accountDto, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> removeAccount(AccountDeletionRequest accountDeletionRequest) {
        try {
            if(!userExists(accountDeletionRequest.username())) {
                throw new UserAccountNotFoundException();
            }
            repository.deleteById(repository.findByUsername(accountDeletionRequest.username()).getAccountId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> creditAccount(CreditDebitRequest request) {
        try {
            Account account = repository.findById(request.accountId()).orElseThrow(UserAccountNotFoundException::new);
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
            Account account = repository.findById(request.accountId()).orElseThrow(UserAccountNotFoundException::new);
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


}
