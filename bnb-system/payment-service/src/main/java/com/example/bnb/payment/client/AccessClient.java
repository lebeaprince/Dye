package com.example.bnb.payment.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "access-service", url = "${bnb.services.access.base-url}")
public interface AccessClient {
  record GrantAccessRequest(
      @NotNull Long bookingId,
      @NotBlank String smartLockId,
      @NotBlank String guestPhoneNumber,
      @NotNull OffsetDateTime validFrom,
      @NotNull OffsetDateTime validTo
  ) {}

  @PostMapping("/access/grants")
  void grant(@RequestBody GrantAccessRequest request);
}
