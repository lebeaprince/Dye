package com.example.bnb.booking.api;

import com.example.bnb.booking.domain.Booking;
import com.example.bnb.booking.domain.ServicePackage;
import com.example.bnb.booking.domain.StayDurationOption;
import com.example.bnb.booking.service.BookingStore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingsController {
  private final BookingStore store;

  public BookingsController(BookingStore store) {
    this.store = store;
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
  public Booking create(@Valid @RequestBody CreateBookingRequest req) {
    return store.createBooking(
        req.roomId(),
        req.guestId(),
        req.startAt(),
        req.stayDuration(),
        req.durationUnits(),
        req.servicePackage()
    );
  }

  @GetMapping("/{bookingId}")
  public Booking get(@PathVariable long bookingId) {
    return store.getBooking(bookingId);
  }

  @PostMapping("/{bookingId}/cancel")
  public Booking cancel(@PathVariable long bookingId) {
    return store.cancelBooking(bookingId);
  }

  @PostMapping("/{bookingId}/confirm")
  public Booking confirm(@PathVariable long bookingId) {
    return store.confirmBooking(bookingId);
  }
}
