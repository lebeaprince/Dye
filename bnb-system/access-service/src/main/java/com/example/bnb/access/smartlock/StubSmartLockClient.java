package com.example.bnb.access.smartlock;

import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StubSmartLockClient implements SmartLockClient {
  private static final Logger log = LoggerFactory.getLogger(StubSmartLockClient.class);

  @Override
  public void grantAccess(String smartLockId, String guestPhoneNumber, OffsetDateTime validFrom, OffsetDateTime validTo) {
    log.info("[SMARTLOCK][STUB] grantAccess lockId={} guest={} from={} to={}", smartLockId, guestPhoneNumber, validFrom, validTo);
  }

  @Override
  public void revokeAccess(String smartLockId, String guestPhoneNumber) {
    log.info("[SMARTLOCK][STUB] revokeAccess lockId={} guest={}", smartLockId, guestPhoneNumber);
  }
}
