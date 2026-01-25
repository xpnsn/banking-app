package com.safevault.security.service;

import com.safevault.security.dto.ApiMessageResponse;
import com.safevault.security.dto.LoginRequest;
import com.safevault.security.dto.MessageResponse;
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
import java.util.Objects;

@Service
public class AuthService {

    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DtoMapper mapper;
    private final KafkaProducer producer;

    public AuthService(CustomUserDetailsService userDetailsService, AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder, DtoMapper mapper, KafkaProducer producer) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.producer = producer;
    }

    public ResponseEntity<?> saveUser(RegistrationRequest user) {
        try {
            UserEntity userEntity = new UserEntity(
                    null,
                    user.username(),
                    user.email(),
                    "+91"+user.phoneNumber(),
                    user.firstName() + " " + user.lastName(),
                    passwordEncoder.encode(user.password()),
                    List.of("USER"),
                    new HashSet<>(),
                    false,
                    false
            );
            userEntity = userRepository.save(userEntity);
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.username());
            String jwtToken = jwtService.generateToken(userDetails);
            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiMessageResponse.of(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    public ResponseEntity<?> validate(String authHeader) {

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiMessageResponse.of("Invalid token", HttpStatus.UNAUTHORIZED));
        }
        String token = authHeader.substring(7);
        if(jwtService.validateToken(token)) {
            return ResponseEntity.ok(ApiMessageResponse.of("VALID", HttpStatus.OK));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiMessageResponse.of("INVALID", HttpStatus.UNAUTHORIZED));

    }

    public ResponseEntity<?> generateToken(LoginRequest user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.username(), user.password()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.username());
            String jwtToken = jwtService.generateToken(userDetails);
            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiMessageResponse.of("BAD CREDENTIALS", HttpStatus.UNAUTHORIZED));
        }
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

    public ResponseEntity<?> addAccountToUser(String accountId) {
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity user = userRepository.findByUsername(name);
            if(user == null || accountId == null) {return new ResponseEntity<>(new MessageResponse("INVALID ID"), HttpStatus.NOT_FOUND);}
            user.getAccountIds().add(Long.valueOf(accountId));
            userRepository.save(user);
            return new ResponseEntity<>(new MessageResponse("SUCCESS"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> removeAccountFromUser(String accountId, String password) {
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity user = userRepository.findByUsername(name);
            if(user == null || accountId == null || !user.getAccountIds().contains(Long.valueOf(accountId))) {return new ResponseEntity<>("Invalid Account ID", HttpStatus.NOT_FOUND);}
            if(!user.getPassword().equals(passwordEncoder.encode(password))) {
                return new ResponseEntity<>(new MessageResponse("Invalid password"), HttpStatus.UNAUTHORIZED);
            }
            user.getAccountIds().remove(Long.valueOf(accountId));
            userRepository.save(user);
            return new ResponseEntity<>(new MessageResponse("SUCCESS"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getPhoneNumber(String userId) {
        String phoneNumber = Objects.requireNonNull(userRepository.findById(Long.valueOf(userId)).orElse(null)).getPhoneNumber();
        return ResponseEntity.ok().body(new MessageResponse(phoneNumber));
    }

    public ResponseEntity<?> kafkaTest(String message) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username);
//        producer.send(user.getPhoneNumber(), "SMS", message, 1);
        return ResponseEntity.ok(ApiMessageResponse.of("SENT!", HttpStatus.OK));
    }
}
