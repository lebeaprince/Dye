package com.example.bnb.notification.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StubSmsGateway {
  private static final Logger log = LoggerFactory.getLogger(StubSmsGateway.class);

  public void send(String to, String message) {
    log.info("[SMS][STUB] to={} message={}", to, message);
  }
}
