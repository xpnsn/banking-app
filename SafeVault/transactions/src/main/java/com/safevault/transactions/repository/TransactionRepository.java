package com.safevault.transactions.repository;

import com.safevault.transactions.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByFromAccountIdOrToAccountId(Long accountFrom, Long accountTo, Pageable pageable);
    Page<Transaction> findByFromAccountId(Long accountFrom, Pageable pageable);
    Page<Transaction> findByToAccountId(Long accountTo, Pageable pageable);

    Page<Transaction> findByFromAccountIdAndTimeStampBetweenOrToAccountIdAndTimeStampBetween(
            Long accountFrom,
            LocalDateTime startDate1,
            LocalDateTime endDate1,
            Long accountTo,
            LocalDateTime startDate2,
            LocalDateTime endDate2,
            Pageable pageable
    );
}
