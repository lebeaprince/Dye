package com.example.bnb.booking.domain;

import java.time.OffsetDateTime;

public record Booking(
    long id,
    long roomId,
    long guestId,
    OffsetDateTime startAt,
    OffsetDateTime endAt,
    StayDurationOption stayDuration,
    int durationUnits,
    ServicePackage servicePackage,
    BookingStatus status
) {}
