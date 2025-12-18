package com.example.bnb.access.smartlock;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoutingSmartLockClientTest {
  @Test
  void routesYaleIdsToYaleClient_andStripsPrefix() {
    RecordingClient yale = new RecordingClient("yale");
    RecordingClient seam = new RecordingClient("seam");
    RoutingSmartLockClient router = new RoutingSmartLockClient(yale, seam);

    OffsetDateTime from = OffsetDateTime.parse("2025-01-01T10:00:00Z");
    OffsetDateTime to = OffsetDateTime.parse("2025-01-02T10:00:00Z");

    String grantId = router.grantAccess(9001L, "yale:lock-1", "+15551234567", from, to);

    assertEquals("yale:grant-1", grantId);
    assertEquals(List.of("grantAccess bookingId=9001 lockId=lock-1"), yale.calls);
    assertEquals(List.of(), seam.calls);
  }

  @Test
  void routesNonYaleIdsToSeamClient_byDefault() {
    RecordingClient yale = new RecordingClient("yale");
    RecordingClient seam = new RecordingClient("seam");
    RoutingSmartLockClient router = new RoutingSmartLockClient(yale, seam);

    OffsetDateTime from = OffsetDateTime.parse("2025-01-01T10:00:00Z");
    OffsetDateTime to = OffsetDateTime.parse("2025-01-02T10:00:00Z");

    String grantId = router.grantAccess(9002L, "some-other-device-id", "+15551234567", from, to);

    assertEquals("seam:grant-1", grantId);
    assertEquals(List.of(), yale.calls);
    assertEquals(List.of("grantAccess bookingId=9002 lockId=some-other-device-id"), seam.calls);
  }

  @Test
  void revokeRoutesToSameProvider() {
    RecordingClient yale = new RecordingClient("yale");
    RecordingClient seam = new RecordingClient("seam");
    RoutingSmartLockClient router = new RoutingSmartLockClient(yale, seam);

    router.revokeAccess("seam:dev_123", "ac_1");
    router.revokeAccess("yale:lock-9", "grant_9");

    assertEquals(List.of("revokeAccess lockId=dev_123 externalGrantId=ac_1"), seam.calls);
    assertEquals(List.of("revokeAccess lockId=lock-9 externalGrantId=grant_9"), yale.calls);
  }

  private static final class RecordingClient implements SmartLockClient {
    private final String name;
    private final List<String> calls = new ArrayList<>();
    private int grantSeq = 0;

    private RecordingClient(String name) {
      this.name = name;
    }

    @Override
    public String grantAccess(long bookingId, String smartLockId, String guestPhoneNumber, OffsetDateTime validFrom, OffsetDateTime validTo) {
      calls.add("grantAccess bookingId=" + bookingId + " lockId=" + smartLockId);
      grantSeq++;
      return name + ":grant-" + grantSeq;
    }

    @Override
    public void revokeAccess(String smartLockId, String externalGrantId) {
      calls.add("revokeAccess lockId=" + smartLockId + " externalGrantId=" + externalGrantId);
    }
  }
}

