package com.example.bnb.payment.client;

import java.time.OffsetDateTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "booking-service", url = "${bnb.services.booking.base-url}")
public interface BookingClient {
  record Booking(
      long id,
      long roomId,
      long guestId,
      OffsetDateTime startAt,
      OffsetDateTime endAt,
      String stayDuration,
      int durationUnits,
      String servicePackage,
      String status
  ) {}

  record Room(long id, String roomNumber, String smartLockId, boolean active) {}

  record Guest(long id, String fullName, String phoneNumber, boolean smsOptIn) {}

  @GetMapping("/bookings/{bookingId}")
  Booking getBooking(@PathVariable("bookingId") long bookingId);

  @PostMapping("/bookings/{bookingId}/confirm")
  Booking confirmBooking(@PathVariable("bookingId") long bookingId);

  @GetMapping("/rooms/{roomId}")
  Room getRoom(@PathVariable("roomId") long roomId);

  @GetMapping("/guests/{guestId}")
  Guest getGuest(@PathVariable("guestId") long guestId);
}
