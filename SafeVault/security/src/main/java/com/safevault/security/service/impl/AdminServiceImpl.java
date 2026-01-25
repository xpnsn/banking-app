package com.safevault.security.service.impl;

import com.safevault.security.dto.ApiMessageResponse;
import com.safevault.security.entity.UserEntity;
import com.safevault.security.repository.UserRepository;
import com.safevault.security.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    UserRepository userRepository;

    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<?> addAdmin(String username) {
        try {
            UserEntity user = userRepository.findByUsername(username);
            List<String> roles = user.getRoles();
            roles.add("ADMIN");
            user.setRoles(roles);
            userRepository.save(user);
            return ResponseEntity.ok(ApiMessageResponse.of("UPDATED!", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiMessageResponse.of(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @Override
    public ResponseEntity<?> removeAdmin(String username) {

        try {
            UserEntity user = userRepository.findByUsername(username);
            List<String> roles = user.getRoles();
            roles.remove("ADMIN");
            user.setRoles(roles);
            userRepository.save(user);
            return ResponseEntity.ok(ApiMessageResponse.of("UPDATED!", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiMessageResponse.of(e.getMessage(), HttpStatus.BAD_REQUEST));
        }

    }
}
