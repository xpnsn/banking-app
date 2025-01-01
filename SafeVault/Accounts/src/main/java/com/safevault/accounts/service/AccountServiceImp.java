package com.safevault.accounts.service;

import com.safevault.accounts.dto.*;
import com.safevault.accounts.exception.AccountNotFoundException;
import com.safevault.accounts.exception.IncorrectPinException;
import com.safevault.accounts.exception.InsufficientBalanceException;
import com.safevault.accounts.exception.UserAccountNotFoundException;
import com.safevault.accounts.model.Account;
import com.safevault.accounts.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service

public class AccountServiceImp implements AccountService {

    private final AccountRepository repository;
    private final AccountDtoMapper mapper;

    public AccountServiceImp(AccountRepository repository, AccountDtoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
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
    public ResponseEntity<?> addAccount(AccountCreationRequest accountCreationRequest) {
        Account account = new Account(
                accountCreationRequest.accountHolderName(),
                accountCreationRequest.accountType(),
                accountCreationRequest.pin(),
                accountCreationRequest.userId()
        );
        repository.save(account);
        AccountDto accountDto = mapper.apply(account);
        return new ResponseEntity<>(accountDto, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> removeAccount(AccountDeletionRequest accountDeletionRequest) {
        try {
            if(!accountExist(accountDeletionRequest.username())) {
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
