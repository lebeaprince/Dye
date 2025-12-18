package com.example.bnb.access.domain;

import java.time.OffsetDateTime;

public record AccessGrant(
    long id,
    long bookingId,
    String smartLockId,
    String externalGrantId,
    String guestPhoneNumber,
    OffsetDateTime validFrom,
    OffsetDateTime validTo,
    AccessGrantStatus status,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
