package com.safevault.accounts.repository;

import com.safevault.accounts.dto.AccountDto;
import com.safevault.accounts.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByUsername(String username);

}
