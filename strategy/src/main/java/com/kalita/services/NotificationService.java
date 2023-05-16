package com.kalita.services;

import com.kalita.model.Recipient;
import com.kalita.model.RecipientType;

public interface NotificationService {

    void sendMessage(Recipient recipient, String message);

    RecipientType getType();
}
