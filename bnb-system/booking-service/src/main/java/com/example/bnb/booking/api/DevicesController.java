package com.example.bnb.booking.api;

import com.example.bnb.booking.domain.SmartDevice;
import com.example.bnb.booking.domain.SmartDeviceType;
import com.example.bnb.booking.service.BookingStore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/devices")
public class DevicesController {
  private final BookingStore store;

  public DevicesController(BookingStore store) {
    this.store = store;
  }

  public record CreateDeviceRequest(
      @NotNull Long roomId,
      @NotNull SmartDeviceType deviceType,
      @NotBlank String name,
      @NotBlank String externalId
  ) {}

  @PostMapping
  public SmartDevice create(
      @RequestHeader(value = "X-Bnb-Slug", required = false) String bnbSlug,
      @Valid @RequestBody CreateDeviceRequest req
  ) {
    return store.addDevice(
        BookingStore.normalizeBnbSlug(bnbSlug),
        req.roomId(),
        req.deviceType(),
        req.name(),
        req.externalId()
    );
  }

  @GetMapping
  public List<SmartDevice> list(
      @RequestHeader(value = "X-Bnb-Slug", required = false) String bnbSlug,
      @RequestParam(value = "roomId", required = false) Long roomId
  ) {
    return store.listDevices(BookingStore.normalizeBnbSlug(bnbSlug), roomId);
  }

  @DeleteMapping("/{deviceId}")
  public SmartDevice remove(
      @RequestHeader(value = "X-Bnb-Slug", required = false) String bnbSlug,
      @PathVariable long deviceId
  ) {
    return store.removeDevice(BookingStore.normalizeBnbSlug(bnbSlug), deviceId);
  }
}
