package com.safevault.security.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Table(
        name = "Users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username", name = "unique_username"),
                @UniqueConstraint(columnNames = "phone_number", name = "unique_phone_number"),
                @UniqueConstraint(columnNames = "email", name = "unique_email_constraint")
        }
)
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private List<String> roles;

    private Set<Long> accountIds = new HashSet<>();

    private Boolean verifiedPhoneNumber;
    private Boolean verifiedEmailAddress;

}
