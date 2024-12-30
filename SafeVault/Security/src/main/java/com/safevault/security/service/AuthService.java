package com.safevault.security.service;

import com.safevault.security.entity.UserEntity;
import com.safevault.security.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> saveUser(UserEntity user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(List.of("USER"));
            userRepository.save(user);
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

    public ResponseEntity<?> generateToken(UserEntity user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
//            UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
            String jwtToken = jwtService.generateToken(user.getUsername());
            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
