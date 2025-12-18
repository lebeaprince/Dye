package com.example.bnb.notification.service;

import com.example.bnb.notification.domain.SmsMessage;
import com.example.bnb.notification.domain.SmsStatus;
import com.example.bnb.notification.sms.StubSmsGateway;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class SmsStore {
  private final AtomicLong smsIdSeq = new AtomicLong(6000);
  private final ConcurrentMap<Long, SmsMessage> messages = new ConcurrentHashMap<>();

  private final StubSmsGateway gateway;

  public SmsStore(StubSmsGateway gateway) {
    this.gateway = gateway;
  }

  public SmsMessage send(String to, String message) {
    if (to == null || to.isBlank()) {
      throw new IllegalArgumentException("to is required");
    }
    if (message == null || message.isBlank()) {
      throw new IllegalArgumentException("message is required");
    }

    long id = smsIdSeq.incrementAndGet();
    OffsetDateTime now = OffsetDateTime.now();

    // Stubbed - in real life this would call Twilio/AWS SNS/etc.
    gateway.send(to, message);

    SmsMessage sms = new SmsMessage(id, to, message, SmsStatus.SENT, now);
    messages.put(id, sms);
    return sms;
  }

  public SmsMessage get(long id) {
    SmsMessage m = messages.get(id);
    if (m == null) {
      throw new NoSuchElementException("SMS not found: " + id);
    }
    return m;
  }

  public List<SmsMessage> list() {
    return new ArrayList<>(messages.values());
  }
}
