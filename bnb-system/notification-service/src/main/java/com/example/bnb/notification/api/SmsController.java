package com.example.bnb.notification.api;

import com.example.bnb.notification.domain.SmsMessage;
import com.example.bnb.notification.service.SmsStore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsController {
  private final SmsStore store;

  public SmsController(SmsStore store) {
    this.store = store;
  }

  public record SendSmsRequest(@NotBlank String to, @NotBlank String message) {}

  @PostMapping
  public SmsMessage send(@Valid @RequestBody SendSmsRequest req) {
    return store.send(req.to(), req.message());
  }

  @GetMapping
  public List<SmsMessage> list() {
    return store.list();
  }

  @GetMapping("/{smsId}")
  public SmsMessage get(@PathVariable long smsId) {
    return store.get(smsId);
  }
}
