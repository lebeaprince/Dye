package com.example.bnb.payment.service;

import com.example.bnb.payment.client.AccessClient;
import com.example.bnb.payment.client.BookingClient;
import com.example.bnb.payment.domain.Payment;
import com.example.bnb.payment.domain.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentOrchestrator {
  private static final Logger log = LoggerFactory.getLogger(PaymentOrchestrator.class);

  private final PaymentStore store;
  private final BookingClient bookingClient;
  private final AccessClient accessClient;

  public PaymentOrchestrator(PaymentStore store, BookingClient bookingClient, AccessClient accessClient) {
    this.store = store;
    this.bookingClient = bookingClient;
    this.accessClient = accessClient;
  }

  /**
   * Marks the payment as captured and (best-effort) provisions smartlock access.
   *
   * Provisioning flow:
   * - confirm booking
   * - fetch booking + room + guest
   * - grant access via access-service (which will notify via SMS)
   */
  public Payment captureAndProvision(long paymentId) {
    Payment payment = store.capture(paymentId);
    if (payment.status() != PaymentStatus.CAPTURED) {
      return payment;
    }

    try {
      BookingClient.Booking booking = bookingClient.confirmBooking(payment.bookingId());
      BookingClient.Room room = bookingClient.getRoom(booking.roomId());
      BookingClient.Guest guest = bookingClient.getGuest(booking.guestId());

      accessClient.grant(new AccessClient.GrantAccessRequest(
          booking.id(),
          room.smartLockId(),
          guest.phoneNumber(),
          booking.startAt(),
          booking.endAt()
      ));
    } catch (Exception e) {
      // keep payment captured even if downstream services fail
      log.warn("Payment captured but provisioning failed for paymentId={}: {}", paymentId, e.toString());
    }

    return payment;
  }
}
