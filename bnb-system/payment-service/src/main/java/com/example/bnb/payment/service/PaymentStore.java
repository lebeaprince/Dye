package com.example.bnb.payment.service;

import com.example.bnb.payment.domain.Payment;
import com.example.bnb.payment.domain.PaymentMethod;
import com.example.bnb.payment.domain.PaymentStatus;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class PaymentStore {
  private final AtomicLong paymentIdSeq = new AtomicLong(4000);
  private final ConcurrentMap<Long, Payment> payments = new ConcurrentHashMap<>();

  public Payment create(long bookingId, long amountCents, String currency, PaymentMethod method) {
    if (bookingId <= 0) {
      throw new IllegalArgumentException("bookingId must be > 0");
    }
    if (amountCents <= 0) {
      throw new IllegalArgumentException("amountCents must be > 0");
    }
    if (currency == null || currency.isBlank()) {
      throw new IllegalArgumentException("currency is required");
    }

    long id = paymentIdSeq.incrementAndGet();
    OffsetDateTime now = OffsetDateTime.now();

    Payment payment = new Payment(
        id,
        bookingId,
        amountCents,
        currency.toUpperCase(),
        method,
        PaymentStatus.PENDING,
        now,
        now
    );
    payments.put(id, payment);
    return payment;
  }

  public Payment get(long paymentId) {
    Payment p = payments.get(paymentId);
    if (p == null) {
      throw new NoSuchElementException("Payment not found: " + paymentId);
    }
    return p;
  }

  public List<Payment> listByBookingId(long bookingId) {
    List<Payment> out = new ArrayList<>();
    for (Payment p : payments.values()) {
      if (p.bookingId() == bookingId) {
        out.add(p);
      }
    }
    return out;
  }

  public Payment capture(long paymentId) {
    return payments.compute(paymentId, (id, existing) -> {
      if (existing == null) {
        throw new NoSuchElementException("Payment not found: " + paymentId);
      }
      if (existing.status() == PaymentStatus.CANCELLED) {
        throw new IllegalStateException("Cannot capture a cancelled payment");
      }
      if (existing.status() == PaymentStatus.REFUNDED) {
        throw new IllegalStateException("Cannot capture a refunded payment");
      }
      if (existing.status() == PaymentStatus.CAPTURED) {
        return existing;
      }

      OffsetDateTime now = OffsetDateTime.now();
      return new Payment(
          existing.id(),
          existing.bookingId(),
          existing.amountCents(),
          existing.currency(),
          existing.method(),
          PaymentStatus.CAPTURED,
          existing.createdAt(),
          now
      );
    });
  }

  public Payment cancel(long paymentId) {
    return payments.compute(paymentId, (id, existing) -> {
      if (existing == null) {
        throw new NoSuchElementException("Payment not found: " + paymentId);
      }
      if (existing.status() == PaymentStatus.CAPTURED) {
        throw new IllegalStateException("Cannot cancel a captured payment");
      }
      OffsetDateTime now = OffsetDateTime.now();
      return new Payment(
          existing.id(),
          existing.bookingId(),
          existing.amountCents(),
          existing.currency(),
          existing.method(),
          PaymentStatus.CANCELLED,
          existing.createdAt(),
          now
      );
    });
  }
}
