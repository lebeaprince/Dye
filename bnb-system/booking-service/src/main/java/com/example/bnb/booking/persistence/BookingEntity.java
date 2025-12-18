package com.example.bnb.booking.persistence;

import com.example.bnb.booking.domain.BookingStatus;
import com.example.bnb.booking.domain.ServicePackage;
import com.example.bnb.booking.domain.StayDurationOption;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "bookings")
public class BookingEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "bnb_slug", nullable = false)
  private String bnbSlug;

  @Column(name = "room_id", nullable = false)
  private Long roomId;

  @Column(name = "guest_id", nullable = false)
  private Long guestId;

  @Column(name = "start_at", nullable = false)
  private OffsetDateTime startAt;

  @Column(name = "end_at", nullable = false)
  private OffsetDateTime endAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "stay_duration", nullable = false)
  private StayDurationOption stayDuration;

  @Column(name = "duration_units", nullable = false)
  private int durationUnits;

  @Enumerated(EnumType.STRING)
  @Column(name = "service_package", nullable = false)
  private ServicePackage servicePackage;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private BookingStatus status;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  protected BookingEntity() {}

  public BookingEntity(
      String bnbSlug,
      Long roomId,
      Long guestId,
      OffsetDateTime startAt,
      OffsetDateTime endAt,
      StayDurationOption stayDuration,
      int durationUnits,
      ServicePackage servicePackage,
      BookingStatus status
  ) {
    this.bnbSlug = bnbSlug;
    this.roomId = roomId;
    this.guestId = guestId;
    this.startAt = startAt;
    this.endAt = endAt;
    this.stayDuration = stayDuration;
    this.durationUnits = durationUnits;
    this.servicePackage = servicePackage;
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public String getBnbSlug() {
    return bnbSlug;
  }

  public Long getRoomId() {
    return roomId;
  }

  public Long getGuestId() {
    return guestId;
  }

  public OffsetDateTime getStartAt() {
    return startAt;
  }

  public OffsetDateTime getEndAt() {
    return endAt;
  }

  public StayDurationOption getStayDuration() {
    return stayDuration;
  }

  public int getDurationUnits() {
    return durationUnits;
  }

  public ServicePackage getServicePackage() {
    return servicePackage;
  }

  public BookingStatus getStatus() {
    return status;
  }

  public void setStatus(BookingStatus status) {
    this.status = status;
  }
}
