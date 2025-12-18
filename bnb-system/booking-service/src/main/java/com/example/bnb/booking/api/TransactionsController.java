package com.example.bnb.booking.api;

import com.example.bnb.booking.persistence.TransactionLogEntity;
import com.example.bnb.booking.persistence.TransactionLogRepository;
import com.example.bnb.booking.service.BookingStore;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TransactionsController {
  private final TransactionLogRepository logs;

  public TransactionsController(TransactionLogRepository logs) {
    this.logs = logs;
  }

  public record TransactionLog(
      long id,
      String action,
      String actor,
      String message,
      Long roomId,
      Long deviceId,
      Long bookingId,
      Long guestId,
      OffsetDateTime createdAt
  ) {}

  public record ActionCount(String action, long count) {}

  public record DashboardResponse(List<TransactionLog> recent, List<ActionCount> actionCountsLast24h) {}

  @GetMapping("/transactions")
  public List<TransactionLog> list(
      @RequestHeader(value = "X-Bnb-Slug", required = false) String bnbSlug,
      @RequestParam(value = "roomId", required = false) Long roomId,
      @RequestParam(value = "deviceId", required = false) Long deviceId,
      @RequestParam(value = "bookingId", required = false) Long bookingId,
      @RequestParam(value = "guestId", required = false) Long guestId,
      @RequestParam(value = "from", required = false) OffsetDateTime from,
      @RequestParam(value = "to", required = false) OffsetDateTime to,
      @RequestParam(value = "limit", required = false, defaultValue = "50") int limit
  ) {
    int size = Math.max(1, Math.min(limit, 200));
    String slug = BookingStore.normalizeBnbSlug(bnbSlug);
    return logs.search(slug, roomId, deviceId, bookingId, guestId, from, to, PageRequest.of(0, size))
        .stream()
        .map(TransactionsController::toDto)
        .toList();
  }

  @GetMapping("/dashboard")
  public DashboardResponse dashboard(@RequestHeader(value = "X-Bnb-Slug", required = false) String bnbSlug) {
    String slug = BookingStore.normalizeBnbSlug(bnbSlug);
    OffsetDateTime since = OffsetDateTime.now(ZoneOffset.UTC).minusHours(24);

    List<TransactionLog> recent = logs.search(slug, null, null, null, null, null, null, PageRequest.of(0, 20))
        .stream()
        .map(TransactionsController::toDto)
        .toList();

    List<ActionCount> counts = logs.countByActionSince(slug, since)
        .stream()
        .map(c -> new ActionCount(c.getAction(), c.getCount()))
        .toList();

    return new DashboardResponse(recent, counts);
  }

  private static TransactionLog toDto(TransactionLogEntity e) {
    return new TransactionLog(
        e.getId(),
        e.getAction(),
        e.getActor(),
        e.getMessage(),
        e.getRoomId(),
        e.getDeviceId(),
        e.getBookingId(),
        e.getGuestId(),
        e.getCreatedAt()
    );
  }
}

