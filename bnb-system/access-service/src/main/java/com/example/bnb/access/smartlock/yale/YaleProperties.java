package com.example.bnb.access.smartlock.yale;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bnb.smartlock.yale")
public record YaleProperties(
    boolean enabled,
    String baseUrl,
    String accessToken,
    String grantPath,
    String revokePath
) {
  public YaleProperties {
    if (grantPath == null || grantPath.isBlank()) {
      // Path template; uses {deviceId}
      grantPath = "/locks/{deviceId}/access-grants";
    }
    if (revokePath == null || revokePath.isBlank()) {
      // Path template; uses {deviceId} and {grantId}
      revokePath = "/locks/{deviceId}/access-grants/{grantId}/revoke";
    }
  }
}

