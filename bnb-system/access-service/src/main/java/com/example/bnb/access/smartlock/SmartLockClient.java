package com.example.bnb.access.smartlock;

import java.time.OffsetDateTime;

public interface SmartLockClient {
  /**
   * Grants access on a smart lock and returns a provider-specific grant identifier.
   * <p>
   * Examples:
   * - Seam: access_code_id
   * - Yale: grant id / key id (depending on Yale API)
   */
  String grantAccess(long bookingId, String smartLockId, String guestPhoneNumber, OffsetDateTime validFrom, OffsetDateTime validTo);

  /**
   * Revokes a previously created access grant by its provider-specific id.
   */
  void revokeAccess(String smartLockId, String externalGrantId);
}
