package com.example.bnb.access.smartlock;

import java.time.OffsetDateTime;

public interface SmartLockClient {
  void grantAccess(String smartLockId, String guestPhoneNumber, OffsetDateTime validFrom, OffsetDateTime validTo);

  void revokeAccess(String smartLockId, String guestPhoneNumber);
}
