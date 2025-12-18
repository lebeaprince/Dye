package com.example.bnb.access.smartlock;

import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Routes Yale devices to Yale API, and all other manufacturers to Seam API.
 *
 * <p>See {@link SmartLockTarget#parse(String)} for smartLockId format.
 */
public class RoutingSmartLockClient implements SmartLockClient {
  private static final Logger log = LoggerFactory.getLogger(RoutingSmartLockClient.class);

  private final SmartLockClient yaleClient;
  private final SmartLockClient seamClient;

  public RoutingSmartLockClient(SmartLockClient yaleClient, SmartLockClient seamClient) {
    this.yaleClient = yaleClient;
    this.seamClient = seamClient;
  }

  @Override
  public String grantAccess(long bookingId, String smartLockId, String guestPhoneNumber, OffsetDateTime validFrom, OffsetDateTime validTo) {
    SmartLockTarget target = SmartLockTarget.parse(smartLockId);
    return switch (target.provider()) {
      case YALE -> {
        log.debug("[SMARTLOCK][ROUTER] routing grantAccess to YALE deviceId={}", target.deviceId());
        yield yaleClient.grantAccess(bookingId, target.deviceId(), guestPhoneNumber, validFrom, validTo);
      }
      case SEAM -> {
        log.debug("[SMARTLOCK][ROUTER] routing grantAccess to SEAM deviceId={}", target.deviceId());
        yield seamClient.grantAccess(bookingId, target.deviceId(), guestPhoneNumber, validFrom, validTo);
      }
    };
  }

  @Override
  public void revokeAccess(String smartLockId, String externalGrantId) {
    SmartLockTarget target = SmartLockTarget.parse(smartLockId);
    switch (target.provider()) {
      case YALE -> {
        log.debug("[SMARTLOCK][ROUTER] routing revokeAccess to YALE deviceId={}", target.deviceId());
        yaleClient.revokeAccess(target.deviceId(), externalGrantId);
      }
      case SEAM -> {
        log.debug("[SMARTLOCK][ROUTER] routing revokeAccess to SEAM deviceId={}", target.deviceId());
        seamClient.revokeAccess(target.deviceId(), externalGrantId);
      }
    }
  }
}

