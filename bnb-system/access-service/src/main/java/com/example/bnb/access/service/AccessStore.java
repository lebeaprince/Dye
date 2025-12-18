package com.example.bnb.access.service;

import com.example.bnb.access.domain.AccessGrant;
import com.example.bnb.access.domain.AccessGrantStatus;
import com.example.bnb.access.smartlock.SmartLockClient;
import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccessStore {
  private static final Logger log = LoggerFactory.getLogger(AccessStore.class);

  private final AtomicLong grantIdSeq = new AtomicLong(5000);
  private final ConcurrentMap<Long, AccessGrant> grants = new ConcurrentHashMap<>();

  private final SmartLockClient smartLockClient;
  private final NotificationClient notificationClient;

  public AccessStore(SmartLockClient smartLockClient, NotificationClient notificationClient) {
    this.smartLockClient = smartLockClient;
    this.notificationClient = notificationClient;
  }

  public AccessGrant grantAccess(long bookingId, String smartLockId, String guestPhoneNumber, OffsetDateTime validFrom, OffsetDateTime validTo) {
    if (bookingId <= 0) {
      throw new IllegalArgumentException("bookingId must be > 0");
    }
    if (smartLockId == null || smartLockId.isBlank()) {
      throw new IllegalArgumentException("smartLockId is required");
    }
    if (guestPhoneNumber == null || guestPhoneNumber.isBlank()) {
      throw new IllegalArgumentException("guestPhoneNumber is required");
    }
    if (validFrom == null || validTo == null) {
      throw new IllegalArgumentException("validFrom and validTo are required");
    }
    if (!validTo.isAfter(validFrom)) {
      throw new IllegalArgumentException("validTo must be after validFrom");
    }

    long id = grantIdSeq.incrementAndGet();
    OffsetDateTime now = OffsetDateTime.now();
    AccessGrant grant = new AccessGrant(id, bookingId, smartLockId, guestPhoneNumber, validFrom, validTo, AccessGrantStatus.GRANTED, now, now);
    grants.put(id, grant);

    smartLockClient.grantAccess(smartLockId, guestPhoneNumber, validFrom, validTo);

    try {
      notificationClient.sendSms(new NotificationClient.SmsRequest(
          guestPhoneNumber,
          "Your room access has been granted until " + validTo + "."
      ));
    } catch (Exception e) {
      // keep access grant successful even if SMS fails
      log.warn("Failed to send SMS notification for grantId={}: {}", id, e.toString());
    }

    return grant;
  }

  public AccessGrant get(long grantId) {
    AccessGrant g = grants.get(grantId);
    if (g == null) {
      throw new NoSuchElementException("Access grant not found: " + grantId);
    }
    return g;
  }

  public AccessGrant revoke(long grantId) {
    return grants.compute(grantId, (id, existing) -> {
      if (existing == null) {
        throw new NoSuchElementException("Access grant not found: " + grantId);
      }
      if (existing.status() == AccessGrantStatus.REVOKED) {
        return existing;
      }

      smartLockClient.revokeAccess(existing.smartLockId(), existing.guestPhoneNumber());

      try {
        notificationClient.sendSms(new NotificationClient.SmsRequest(
            existing.guestPhoneNumber(),
            "Your room access has been revoked."
        ));
      } catch (Exception e) {
        log.warn("Failed to send SMS notification for revoke grantId={}: {}", grantId, e.toString());
      }

      OffsetDateTime now = OffsetDateTime.now();
      return new AccessGrant(
          existing.id(),
          existing.bookingId(),
          existing.smartLockId(),
          existing.guestPhoneNumber(),
          existing.validFrom(),
          existing.validTo(),
          AccessGrantStatus.REVOKED,
          existing.createdAt(),
          now
      );
    });
  }
}
