package com.example.bnb.access.service;

import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "${bnb.services.notification.base-url}")
public interface NotificationClient {
  record SmsRequest(@NotBlank String to, @NotBlank String message) {}

  @PostMapping("/sms")
  void sendSms(@RequestBody SmsRequest request);
}
