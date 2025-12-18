package com.example.bnb.access.smartlock;

import java.util.Locale;

/**
 * Parses a smart lock id into a provider + provider-specific device id.
 *
 * <p>Routing rules:
 * - "yale:&lt;deviceId&gt;" routes to Yale API
 * - "seam:&lt;deviceId&gt;" routes to Seam API
 * - otherwise defaults to Seam (for non-Yale manufacturers)
 */
public record SmartLockTarget(SmartLockProvider provider, String deviceId) {
  public static SmartLockTarget parse(String smartLockId) {
    if (smartLockId == null || smartLockId.isBlank()) {
      throw new IllegalArgumentException("smartLockId is required");
    }

    String trimmed = smartLockId.trim();
    int idx = trimmed.indexOf(':');
    if (idx <= 0 || idx == trimmed.length() - 1) {
      // no prefix (or malformed) => default to Seam
      return new SmartLockTarget(SmartLockProvider.SEAM, trimmed);
    }

    String prefix = trimmed.substring(0, idx).toLowerCase(Locale.ROOT);
    String deviceId = trimmed.substring(idx + 1);

    return switch (prefix) {
      case "yale" -> new SmartLockTarget(SmartLockProvider.YALE, deviceId);
      case "seam" -> new SmartLockTarget(SmartLockProvider.SEAM, deviceId);
      default -> new SmartLockTarget(SmartLockProvider.SEAM, trimmed);
    };
  }
}

