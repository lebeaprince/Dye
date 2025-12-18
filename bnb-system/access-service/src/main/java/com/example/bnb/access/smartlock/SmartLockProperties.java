package com.example.bnb.access.smartlock;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bnb.smartlock")
public record SmartLockProperties(
    SmartLockMode mode
) {
  public SmartLockProperties {
    if (mode == null) {
      mode = SmartLockMode.STUB;
    }
  }
}

