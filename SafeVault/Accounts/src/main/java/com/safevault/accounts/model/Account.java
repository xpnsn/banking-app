package com.safevault.accounts.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(unique = true, nullable = false)
    String email;

    @Column(nullable = false)
    private String pin;

    @Column(nullable = false, unique = true)
    private Long mobileNumber;

    @Column(nullable = false, unique = true)
    private String username;

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
    private String password;

    public Account(Long mobileNumber, String username, String emailqw, String accountHolderName, AccountType accountType, String password, String pin) {
        this.mobileNumber = mobileNumber;
        this.username = username;
        this.email = email;
        this.balance = 0.0;
        this.createdAt = LocalDateTime.now();
        this.accountHolderName = accountHolderName;
        this.accountType = accountType;
        this.password = password;
        this.pin = pin;
    }
}
