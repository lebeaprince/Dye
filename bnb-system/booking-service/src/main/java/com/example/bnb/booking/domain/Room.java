package com.example.bnb.booking.domain;

public record Room(
    long id,
    String roomNumber,
    String smartLockId,
    boolean active
) {}
