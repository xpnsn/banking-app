package com.safevault.notifications.service;

import com.safevault.notifications.dto.MessageRequest;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties(prefix = "twilio")
@Slf4j
public class NotificationServiceImp implements NotificationService {

    Logger log = LoggerFactory.getLogger(NotificationServiceImp.class);


    private final String accountSid = "ACa31ec31aeaf0c9f8af0685592655b8a2";
    private final String authToken = "9f860bb68b2b29c0680e7588ae35b68f";
    private final String phoneNumber = "+12185208022";

    @PostConstruct
    private void setup() {
        log.info("Account SID: {}", accountSid);
        log.info("AuthToken: {}", authToken);
        log.info("Phone Number: {}", phoneNumber);
        Twilio.init(accountSid, authToken);
    }

    @Override
    public ResponseEntity<?> notifyViaSms(MessageRequest request) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(request.contactNumber()),
                    new PhoneNumber(phoneNumber),
                    request.messageBody()
            ).create();

            return new ResponseEntity<>(message.getStatus().toString(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
//        return new ResponseEntity<>("done", HttpStatus.OK);
    }
}
