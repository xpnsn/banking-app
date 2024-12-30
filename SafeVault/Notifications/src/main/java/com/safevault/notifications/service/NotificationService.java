package com.safevault.notifications.service;

import com.safevault.notifications.dto.MessageRequest;
import org.springframework.http.ResponseEntity;

public interface NotificationService {
    ResponseEntity<?> notifyViaSms(MessageRequest request);
}
