package com.safevault.security.service;

import com.safevault.security.dto.LoginRequest;
import com.safevault.security.dto.RegistrationRequest;
import com.safevault.security.dto.UserDto;
import com.safevault.security.entity.UserEntity;
import com.safevault.security.mapper.DtoMapper;
import com.safevault.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
public class AuthService {

    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DtoMapper mapper;

    public AuthService(CustomUserDetailsService userDetailsService, AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder, DtoMapper mapper) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    public ResponseEntity<?> saveUser(RegistrationRequest user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            UserEntity userEntity = new UserEntity(
                    null,
                    user.getUsername(),
                    user.getEmail(),
                    user.getName(),
                    user.getPassword(),
                    List.of("USER"),
                    new HashSet<>()
            );
            userRepository.save(userEntity);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> validate(String authHeader) {

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        String token = authHeader.substring(7);
        if(jwtService.validateToken(token)) {
            return new ResponseEntity<>("VALID", HttpStatus.OK);
        }
        return new ResponseEntity<>("INVALID", HttpStatus.UNAUTHORIZED);

    }

    public ResponseEntity<?> generateToken(LoginRequest user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.username(), user.password()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.username());
            String jwtToken = jwtService.generateToken(userDetails);
            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    public UserDto getUser(String username) {
        UserEntity user = userRepository.findByUsername(username);
        return mapper.apply(user);
    }

    public ResponseEntity<?> getUser() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(name);

        if(user == null) {return null;}
        return new ResponseEntity<>(mapper.apply(user), HttpStatus.OK);
    }

    public UserEntity getUserByUserId(String id) {
        return userRepository.findById(Long.valueOf(id)).orElse(null);
    }

    public ResponseEntity<String> addAccountToUser(String accountId) {
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity user = userRepository.findByUsername(name);
            if(user == null || accountId == null) {return new ResponseEntity<>("Invalid ID", HttpStatus.NOT_FOUND);}
            user.getAccountIds().add(Long.valueOf(accountId));
            userRepository.save(user);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
