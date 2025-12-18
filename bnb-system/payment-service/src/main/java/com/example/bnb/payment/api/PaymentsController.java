package com.example.bnb.payment.api;

import com.example.bnb.payment.domain.Payment;
import com.example.bnb.payment.domain.PaymentMethod;
import com.example.bnb.payment.service.PaymentOrchestrator;
import com.example.bnb.payment.service.PaymentStore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentsController {
  private final PaymentStore store;
  private final PaymentOrchestrator orchestrator;

  public PaymentsController(PaymentStore store, PaymentOrchestrator orchestrator) {
    this.store = store;
    this.orchestrator = orchestrator;
  }

  public record CreatePaymentRequest(
      @NotNull Long bookingId,
      @NotNull @Min(1) Long amountCents,
      @NotBlank String currency,
      @NotNull PaymentMethod method
  ) {}

  @PostMapping
  public Payment create(@Valid @RequestBody CreatePaymentRequest req) {
    return store.create(req.bookingId(), req.amountCents(), req.currency(), req.method());
  }

  @GetMapping("/{paymentId}")
  public Payment get(@PathVariable long paymentId) {
    return store.get(paymentId);
  }

  @GetMapping
  public List<Payment> list(@RequestParam(name = "bookingId") long bookingId) {
    return store.listByBookingId(bookingId);
  }

  @PostMapping("/{paymentId}/capture")
  public Payment capture(@PathVariable long paymentId) {
    return orchestrator.captureAndProvision(paymentId);
  }

  @PostMapping("/{paymentId}/cancel")
  public Payment cancel(@PathVariable long paymentId) {
    return store.cancel(paymentId);
  }
}
