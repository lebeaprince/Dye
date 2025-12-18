package com.example.bnb.payment.domain;

import java.time.OffsetDateTime;

public record Payment(
    long id,
    long bookingId,
    long amountCents,
    String currency,
    PaymentMethod method,
    PaymentStatus status,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
