package com.example.bnb.booking.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "transaction_logs")
public class TransactionLogEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "bnb_slug", nullable = false)
  private String bnbSlug;

  @Column(name = "actor")
  private String actor;

  @Column(name = "action", nullable = false)
  private String action;

  @Column(name = "message")
  private String message;

  @Column(name = "room_id")
  private Long roomId;

  @Column(name = "device_id")
  private Long deviceId;

  @Column(name = "booking_id")
  private Long bookingId;

  @Column(name = "guest_id")
  private Long guestId;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  protected TransactionLogEntity() {}

  public TransactionLogEntity(
      String bnbSlug,
      String actor,
      String action,
      String message,
      Long roomId,
      Long deviceId,
      Long bookingId,
      Long guestId
  ) {
    this.bnbSlug = bnbSlug;
    this.actor = actor;
    this.action = action;
    this.message = message;
    this.roomId = roomId;
    this.deviceId = deviceId;
    this.bookingId = bookingId;
    this.guestId = guestId;
  }

  public Long getId() {
    return id;
  }

  public String getBnbSlug() {
    return bnbSlug;
  }

  public String getActor() {
    return actor;
  }

  public String getAction() {
    return action;
  }

  public String getMessage() {
    return message;
  }

  public Long getRoomId() {
    return roomId;
  }

  public Long getDeviceId() {
    return deviceId;
  }

  public Long getBookingId() {
    return bookingId;
  }

  public Long getGuestId() {
    return guestId;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }
}

