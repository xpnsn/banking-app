package com.safevault.accounts.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity(name = "accounts")
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(unique = true)
    private Long userId;

    private boolean isVerified;

    public Account(String accountHolderName, AccountType accountType, Long userId, String pin) {
        this.accountHolderName = accountHolderName;
        this.accountType = accountType;
        this.pin = pin;
        this.userId = userId;
        this.balance = 0.0;
        this.createdAt = LocalDateTime.now();
        this.isVerified = false;
    }
}
