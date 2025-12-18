package com.example.bnb.notification.domain;

import java.time.OffsetDateTime;

public record SmsMessage(
    long id,
    String to,
    String message,
    SmsStatus status,
    OffsetDateTime createdAt
) {}
