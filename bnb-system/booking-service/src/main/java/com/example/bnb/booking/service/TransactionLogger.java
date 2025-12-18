package com.example.bnb.booking.service;

import com.example.bnb.booking.persistence.TransactionLogEntity;
import com.example.bnb.booking.persistence.TransactionLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionLogger {
  private final TransactionLogRepository logs;

  public TransactionLogger(TransactionLogRepository logs) {
    this.logs = logs;
  }

  @Transactional
  public void record(
      String bnbSlug,
      String actor,
      String action,
      String message,
      Long roomId,
      Long deviceId,
      Long bookingId,
      Long guestId
  ) {
    String slug = BookingStore.normalizeBnbSlug(bnbSlug);
    String normalizedActor = (actor == null || actor.isBlank()) ? null : actor.trim();
    logs.save(new TransactionLogEntity(
        slug,
        normalizedActor,
        action,
        message,
        roomId,
        deviceId,
        bookingId,
        guestId
    ));
  }
}

