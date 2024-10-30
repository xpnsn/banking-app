package com.safevault.accounts.service;

import com.safevault.accounts.dto.AccountDto;
import com.safevault.accounts.model.Account;
import com.safevault.accounts.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

@Service
@RequiredArgsConstructor
public class AccountServiceImp implements AccountService {

    private final AccountRepository repository;
    private ModelMapper model;

    @Override
    public boolean userExists(AccountDto accountDto) {

        Account account = model.map(accountDto, Account.class);
        return true;
    }
}
