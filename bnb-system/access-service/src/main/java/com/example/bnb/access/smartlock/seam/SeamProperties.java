package com.example.bnb.access.smartlock.seam;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bnb.smartlock.seam")
public record SeamProperties(
    boolean enabled,
    String baseUrl,
    String apiKey,
    String createAccessCodePath,
    String deleteAccessCodePath
) {
  public SeamProperties {
    if (baseUrl == null || baseUrl.isBlank()) {
      baseUrl = "https://connect.getseam.com";
    }
    if (createAccessCodePath == null || createAccessCodePath.isBlank()) {
      createAccessCodePath = "/access_codes/create";
    }
    if (deleteAccessCodePath == null || deleteAccessCodePath.isBlank()) {
      deleteAccessCodePath = "/access_codes/delete";
    }
  }
}

