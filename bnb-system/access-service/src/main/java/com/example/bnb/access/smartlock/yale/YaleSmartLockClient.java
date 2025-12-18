package com.example.bnb.access.smartlock.yale;

import com.example.bnb.access.smartlock.SmartLockClient;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Yale-specific smart lock client.
 *
 * <p>This client expects to receive a Yale device id (NOT a "yale:"-prefixed id). Routing is handled
 * by {@code RoutingSmartLockClient}.
 *
 * <p>Note: Yale has multiple APIs/product lines. This client uses configurable endpoint paths and
 * a bearer token so it can be adapted to the specific Yale API your deployment uses.
 */
public class YaleSmartLockClient implements SmartLockClient {
  private final RestTemplate restTemplate;
  private final YaleProperties props;

  public YaleSmartLockClient(RestTemplate restTemplate, YaleProperties props) {
    this.restTemplate = restTemplate;
    this.props = props;
  }

  @Override
  public String grantAccess(long bookingId, String smartLockId, String guestPhoneNumber, OffsetDateTime validFrom, OffsetDateTime validTo) {
    ensureEnabledAndConfigured();

    String url = UriComponentsBuilder.fromHttpUrl(props.baseUrl())
        .path(props.grantPath())
        .buildAndExpand(Map.of("deviceId", smartLockId))
        .toUriString();

    GrantAccessRequest body = new GrantAccessRequest(
        bookingId,
        guestPhoneNumber,
        validFrom.toString(),
        validTo.toString()
    );

    // We accept either a strongly-typed response or a generic json map depending on what Yale returns.
    try {
      GrantAccessResponse typed = restTemplate.postForObject(url, new HttpEntity<>(body, authHeaders()), GrantAccessResponse.class);
      String id = (typed == null) ? null : typed.grantId();
      if (StringUtils.hasText(id)) {
        return id;
      }
    } catch (RestClientException ignored) {
      // fall through to Map parsing
    }

    try {
      @SuppressWarnings("unchecked")
      Map<String, Object> map = restTemplate.postForObject(url, new HttpEntity<>(body, authHeaders()), Map.class);
      String id = extractId(map);
      if (!StringUtils.hasText(id)) {
        throw new IllegalStateException("Yale grantAccess failed: missing grant id in response");
      }
      return id;
    } catch (RestClientException e) {
      throw new IllegalStateException("Yale grantAccess failed: " + e.getMessage(), e);
    }
  }

  @Override
  public void revokeAccess(String smartLockId, String externalGrantId) {
    ensureEnabledAndConfigured();
    if (!StringUtils.hasText(externalGrantId)) {
      throw new IllegalArgumentException("externalGrantId is required");
    }

    String url = UriComponentsBuilder.fromHttpUrl(props.baseUrl())
        .path(props.revokePath())
        .buildAndExpand(Map.of("deviceId", smartLockId, "grantId", externalGrantId))
        .toUriString();

    try {
      // Most Yale APIs use POST for state changes; if yours differs, adjust revokePath and/or swap to exchange().
      restTemplate.postForLocation(url, new HttpEntity<>(null, authHeaders()));
    } catch (RestClientException e) {
      throw new IllegalStateException("Yale revokeAccess failed: " + e.getMessage(), e);
    }
  }

  private void ensureEnabledAndConfigured() {
    if (!props.enabled()) {
      throw new IllegalStateException("Yale smart lock integration is disabled (bnb.smartlock.yale.enabled=false)");
    }
    if (!StringUtils.hasText(props.baseUrl())) {
      throw new IllegalStateException("Yale baseUrl is required (bnb.smartlock.yale.base-url)");
    }
    if (!StringUtils.hasText(props.accessToken())) {
      throw new IllegalStateException("Yale accessToken is required (bnb.smartlock.yale.access-token)");
    }
    Objects.requireNonNull(props.grantPath(), "Yale grantPath is required");
    Objects.requireNonNull(props.revokePath(), "Yale revokePath is required");
  }

  private HttpHeaders authHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(MediaType.parseMediaTypes(MediaType.APPLICATION_JSON_VALUE));
    headers.setBearerAuth(props.accessToken());
    return headers;
  }

  private static String extractId(Map<String, Object> map) {
    if (map == null) {
      return null;
    }
    for (String key : new String[]{"grant_id", "grantId", "access_grant_id", "accessGrantId", "id"}) {
      Object v = map.get(key);
      if (v instanceof String s && StringUtils.hasText(s)) {
        return s;
      }
    }
    // sometimes nested, try common nesting keys
    for (String key : new String[]{"grant", "access_grant", "accessGrant"}) {
      Object nested = map.get(key);
      if (nested instanceof Map<?, ?> nestedMap) {
        @SuppressWarnings("unchecked")
        String id = extractId((Map<String, Object>) nestedMap);
        if (StringUtils.hasText(id)) {
          return id;
        }
      }
    }
    return null;
  }

  public record GrantAccessRequest(
      @JsonProperty("booking_id") long bookingId,
      @JsonProperty("guest_phone_number") String guestPhoneNumber,
      @JsonProperty("valid_from") String validFrom,
      @JsonProperty("valid_to") String validTo
  ) {}

  public record GrantAccessResponse(
      @JsonProperty("grant_id") String grantId
  ) {}
}

