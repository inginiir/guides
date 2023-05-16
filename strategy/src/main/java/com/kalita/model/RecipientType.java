package com.kalita.model;

import lombok.Getter;

@Getter
public enum RecipientType {

    TELEGRAM("Telegram", "Chat ID"),
    PHONE("Phone", "Phone number"),
    EMAIL("Email", "Email address");

    RecipientType(String readableName, String addressType) {
        this.readableName = readableName;
        this.addressType = addressType;
    }

    private final String readableName;
    private final String addressType;
}
