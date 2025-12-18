package com.example.bnb.booking.api;

import com.example.bnb.booking.domain.Guest;
import com.example.bnb.booking.service.BookingStore;
import com.example.bnb.booking.service.TransactionLogger;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/guests")
public class GuestsController {
  private final BookingStore store;
  private final TransactionLogger tx;

  public GuestsController(BookingStore store, TransactionLogger tx) {
    this.store = store;
    this.tx = tx;
  }

  public record CreateGuestRequest(
      @NotBlank String fullName,
      @NotBlank String phoneNumber,
      Boolean smsOptIn
  ) {}

  @PostMapping
  public Guest create(
      @RequestHeader(value = "X-Bnb-Slug", required = false) String bnbSlug,
      @RequestHeader(value = "X-Actor", required = false) String actor,
      @Valid @RequestBody CreateGuestRequest req
  ) {
    boolean smsOptIn = req.smsOptIn() == null || req.smsOptIn();
    String slug = BookingStore.normalizeBnbSlug(bnbSlug);
    Guest guest = store.createGuest(slug, req.fullName(), req.phoneNumber(), smsOptIn);
    tx.record(
        slug,
        actor,
        "GUEST_CREATED",
        "Created guest " + guest.fullName(),
        null,
        null,
        null,
        guest.id()
    );
    return guest;
  }

  @GetMapping
  public List<Guest> list(@RequestHeader(value = "X-Bnb-Slug", required = false) String bnbSlug) {
    return store.listGuests(BookingStore.normalizeBnbSlug(bnbSlug));
  }

  @GetMapping("/{guestId}")
  public Guest get(
      @RequestHeader(value = "X-Bnb-Slug", required = false) String bnbSlug,
      @PathVariable long guestId
  ) {
    return store.getGuest(BookingStore.normalizeBnbSlug(bnbSlug), guestId);
  }
}
