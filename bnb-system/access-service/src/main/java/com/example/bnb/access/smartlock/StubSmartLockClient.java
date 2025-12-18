package com.example.bnb.access.smartlock;

import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StubSmartLockClient implements SmartLockClient {
  private static final Logger log = LoggerFactory.getLogger(StubSmartLockClient.class);

  @Override
  public String grantAccess(long bookingId, String smartLockId, String guestPhoneNumber, OffsetDateTime validFrom, OffsetDateTime validTo) {
    String externalGrantId = "stub:" + bookingId + ":" + guestPhoneNumber;
    log.info(
        "[SMARTLOCK][STUB] grantAccess bookingId={} lockId={} guest={} from={} to={} externalGrantId={}",
        bookingId,
        smartLockId,
        guestPhoneNumber,
        validFrom,
        validTo,
        externalGrantId
    );
    return externalGrantId;
  }

  @Override
  public void revokeAccess(String smartLockId, String externalGrantId) {
    log.info("[SMARTLOCK][STUB] revokeAccess lockId={} externalGrantId={}", smartLockId, externalGrantId);
  }
}
