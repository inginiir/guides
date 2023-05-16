package com.kalita.services.impl;

import com.kalita.model.Recipient;
import com.kalita.model.RecipientType;
import com.kalita.services.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService implements NotificationService {

    @Override
    public void sendMessage(Recipient recipient, String message) {
        log.info("Sending message '{}' to {} ({})", message, recipient.getAddress(), recipient.getName());
    }

    @Override
    public RecipientType getType() {
        return RecipientType.EMAIL;
    }
}
