package com.example.bnb.access.smartlock.seam;

import com.example.bnb.access.smartlock.SmartLockClient;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Seam implementation used for all non-Yale smart lock manufacturers.
 *
 * <p>This client expects to receive a Seam device id (NOT a "seam:"-prefixed id). Routing is handled
 * by {@code RoutingSmartLockClient}.
 */
public class SeamSmartLockClient implements SmartLockClient {
  private final RestTemplate restTemplate;
  private final SeamProperties props;

  public SeamSmartLockClient(RestTemplate restTemplate, SeamProperties props) {
    this.restTemplate = restTemplate;
    this.props = props;
  }

  @Override
  public String grantAccess(long bookingId, String smartLockId, String guestPhoneNumber, OffsetDateTime validFrom, OffsetDateTime validTo) {
    ensureEnabledAndConfigured();

    String url = UriComponentsBuilder.fromHttpUrl(props.baseUrl())
        .path(props.createAccessCodePath())
        .toUriString();

    String name = "bnb:" + bookingId + ":" + guestPhoneNumber;
    CreateAccessCodeRequest body = new CreateAccessCodeRequest(
        smartLockId,
        name,
        validFrom.toString(),
        validTo.toString()
    );

    CreateAccessCodeResponse resp;
    try {
      resp = restTemplate.postForObject(url, new HttpEntity<>(body, authHeaders()), CreateAccessCodeResponse.class);
    } catch (RestClientException e) {
      throw new IllegalStateException("Seam grantAccess failed: " + e.getMessage(), e);
    }

    if (resp == null || resp.accessCode() == null || !StringUtils.hasText(resp.accessCode().accessCodeId())) {
      throw new IllegalStateException("Seam grantAccess failed: missing access_code_id in response");
    }
    return resp.accessCode().accessCodeId();
  }

  @Override
  public void revokeAccess(String smartLockId, String externalGrantId) {
    ensureEnabledAndConfigured();

    String url = UriComponentsBuilder.fromHttpUrl(props.baseUrl())
        .path(props.deleteAccessCodePath())
        .toUriString();

    DeleteAccessCodeRequest body = new DeleteAccessCodeRequest(externalGrantId);
    try {
      restTemplate.postForLocation(url, new HttpEntity<>(body, authHeaders()));
    } catch (RestClientException e) {
      throw new IllegalStateException("Seam revokeAccess failed: " + e.getMessage(), e);
    }
  }

  private void ensureEnabledAndConfigured() {
    if (!props.enabled()) {
      throw new IllegalStateException("Seam smart lock integration is disabled (bnb.smartlock.seam.enabled=false)");
    }
    if (!StringUtils.hasText(props.apiKey())) {
      throw new IllegalStateException("Seam apiKey is required (bnb.smartlock.seam.api-key)");
    }
    Objects.requireNonNull(props.baseUrl(), "Seam baseUrl is required");
  }

  private HttpHeaders authHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(MediaType.parseMediaTypes(MediaType.APPLICATION_JSON_VALUE));
    headers.setBearerAuth(props.apiKey());
    return headers;
  }

  public record CreateAccessCodeRequest(
      @JsonProperty("device_id") String deviceId,
      @JsonProperty("name") String name,
      @JsonProperty("starts_at") String startsAt,
      @JsonProperty("ends_at") String endsAt
  ) {}

  public record CreateAccessCodeResponse(
      @JsonProperty("access_code") AccessCode accessCode
  ) {
    public record AccessCode(
        @JsonProperty("access_code_id") String accessCodeId
    ) {}
  }

  public record DeleteAccessCodeRequest(
      @JsonProperty("access_code_id") String accessCodeId
  ) {}
}

