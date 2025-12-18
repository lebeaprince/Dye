package com.example.bnb.booking.domain;

public record SmartDevice(
    long id,
    long roomId,
    SmartDeviceType deviceType,
    String name,
    String externalId,
    boolean active
) {}
