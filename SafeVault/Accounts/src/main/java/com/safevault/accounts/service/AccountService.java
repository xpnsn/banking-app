package com.safevault.accounts.service;

import com.safevault.accounts.dto.AccountDto;

public interface AccountService {

    public boolean userExists(AccountDto accountDto);
}
