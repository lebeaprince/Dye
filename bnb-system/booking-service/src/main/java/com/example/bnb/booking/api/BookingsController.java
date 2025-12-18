package com.example.bnb.booking.api;

import com.example.bnb.booking.domain.Booking;
import com.example.bnb.booking.domain.ServicePackage;
import com.example.bnb.booking.domain.StayDurationOption;
import com.example.bnb.booking.service.BookingStore;
import com.example.bnb.booking.service.TransactionLogger;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingsController {
  private final BookingStore store;
  private final TransactionLogger tx;

  public BookingsController(BookingStore store, TransactionLogger tx) {
    this.store = store;
    this.tx = tx;
  }

  public record CreateBookingRequest(
      @NotNull Long roomId,
      @NotNull Long guestId,
      @NotNull OffsetDateTime startAt,
      @NotNull StayDurationOption stayDuration,
      @NotNull @Min(1) Integer durationUnits,
      @NotNull ServicePackage servicePackage
  ) {}

  @PostMapping
  public Booking create(
      @RequestHeader(value = "X-Bnb-Slug", required = false) String bnbSlug,
      @RequestHeader(value = "X-Actor", required = false) String actor,
      @Valid @RequestBody CreateBookingRequest req
  ) {
    String slug = BookingStore.normalizeBnbSlug(bnbSlug);
    Booking booking = store.createBooking(
        slug,
        req.roomId(),
        req.guestId(),
        req.startAt(),
        req.stayDuration(),
        req.durationUnits(),
        req.servicePackage()
    );
    tx.record(
        slug,
        actor,
        "BOOKING_REQUESTED",
        "Booking requested (status=" + booking.status() + ")",
        booking.roomId(),
        null,
        booking.id(),
        booking.guestId()
    );
    return booking;
  }

  @GetMapping("/{bookingId}")
  public Booking get(
      @RequestHeader(value = "X-Bnb-Slug", required = false) String bnbSlug,
      @PathVariable long bookingId
  ) {
    return store.getBooking(BookingStore.normalizeBnbSlug(bnbSlug), bookingId);
  }

  @PostMapping("/{bookingId}/cancel")
  public Booking cancel(
      @RequestHeader(value = "X-Bnb-Slug", required = false) String bnbSlug,
      @RequestHeader(value = "X-Actor", required = false) String actor,
      @PathVariable long bookingId
  ) {
    String slug = BookingStore.normalizeBnbSlug(bnbSlug);
    Booking booking = store.cancelBooking(slug, bookingId);
    tx.record(
        slug,
        actor,
        "BOOKING_CANCELLED",
        "Booking cancelled",
        booking.roomId(),
        null,
        booking.id(),
        booking.guestId()
    );
    return booking;
  }

  @PostMapping("/{bookingId}/confirm")
  public Booking confirm(
      @RequestHeader(value = "X-Bnb-Slug", required = false) String bnbSlug,
      @RequestHeader(value = "X-Actor", required = false) String actor,
      @PathVariable long bookingId
  ) {
    String slug = BookingStore.normalizeBnbSlug(bnbSlug);
    Booking booking = store.confirmBooking(slug, bookingId);
    tx.record(
        slug,
        actor,
        "BOOKING_CONFIRMED",
        "Booking confirmed",
        booking.roomId(),
        null,
        booking.id(),
        booking.guestId()
    );
    return booking;
  }
}
