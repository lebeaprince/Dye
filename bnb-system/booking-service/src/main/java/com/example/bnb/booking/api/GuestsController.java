package com.example.bnb.booking.api;

import com.example.bnb.booking.domain.Guest;
import com.example.bnb.booking.service.BookingStore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/guests")
public class GuestsController {
  private final BookingStore store;

  public GuestsController(BookingStore store) {
    this.store = store;
  }

  public record CreateGuestRequest(
      @NotBlank String fullName,
      @NotBlank String phoneNumber,
      Boolean smsOptIn
  ) {}

  @PostMapping
  public Guest create(@Valid @RequestBody CreateGuestRequest req) {
    boolean smsOptIn = req.smsOptIn() == null || req.smsOptIn();
    return store.createGuest(req.fullName(), req.phoneNumber(), smsOptIn);
  }

  @GetMapping
  public List<Guest> list() {
    return store.listGuests();
  }

  @GetMapping("/{guestId}")
  public Guest get(@PathVariable long guestId) {
    return store.getGuest(guestId);
  }
}
