package com.safevault.accounts.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Entity(name = "accounts")
@NoArgsConstructor
@Table(name = "accounts", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "account_type"}))
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @SequenceGenerator(name = "account_seq", sequenceName = "account_id_seq", allocationSize = 1)
    private Long accountId;

    @Column(nullable = false)
    private String pin;

    @Column(nullable = false)
    private String accountHolderName;

    @Column(nullable = false)
    private Double balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(nullable = false)
    private boolean isVerified;

    @Column(nullable = false)
    private List<Long> transactionIds;

    public Account(String accountHolderName, AccountType accountType, Long userId, String pin) {
        this.accountHolderName = accountHolderName;
        this.accountType = accountType;
        this.pin = pin;
        this.userId = userId;
        this.balance = 0.0;
        this.createdAt = LocalDateTime.now();
        this.isVerified = false;
        this.status = AccountStatus.INACTIVE;
        this.userId = userId;
        this.transactionIds = Collections.emptyList();
    }
}
