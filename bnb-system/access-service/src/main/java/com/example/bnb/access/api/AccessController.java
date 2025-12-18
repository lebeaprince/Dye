package com.example.bnb.access.api;

import com.example.bnb.access.domain.AccessGrant;
import com.example.bnb.access.service.AccessStore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/access")
public class AccessController {
  private final AccessStore store;

  public AccessController(AccessStore store) {
    this.store = store;
  }

  public record GrantAccessRequest(
      @NotNull Long bookingId,
      @NotBlank String smartLockId,
      @NotBlank String guestPhoneNumber,
      @NotNull OffsetDateTime validFrom,
      @NotNull OffsetDateTime validTo
  ) {}

  @PostMapping("/grants")
  public AccessGrant grant(@Valid @RequestBody GrantAccessRequest req) {
    return store.grantAccess(req.bookingId(), req.smartLockId(), req.guestPhoneNumber(), req.validFrom(), req.validTo());
  }

  @GetMapping("/grants/{grantId}")
  public AccessGrant get(@PathVariable long grantId) {
    return store.get(grantId);
  }

  @PostMapping("/grants/{grantId}/revoke")
  public AccessGrant revoke(@PathVariable long grantId) {
    return store.revoke(grantId);
  }
}
