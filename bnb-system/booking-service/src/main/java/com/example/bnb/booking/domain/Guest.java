package com.example.bnb.booking.domain;

public record Guest(
    long id,
    String fullName,
    String phoneNumber,
    boolean smsOptIn
) {}
