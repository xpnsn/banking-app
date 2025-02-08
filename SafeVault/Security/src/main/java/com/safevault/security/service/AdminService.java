package com.safevault.security.service;

import org.springframework.http.ResponseEntity;

public interface AdminService {

    ResponseEntity<?> addAdmin(String username);

    ResponseEntity<?> removeAdmin(String username);
}
