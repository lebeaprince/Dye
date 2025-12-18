package com.example.bnb.booking.persistence;

import com.example.bnb.booking.domain.SmartDeviceType;
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
@Table(name = "smart_devices")
public class SmartDeviceEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "bnb_slug", nullable = false)
  private String bnbSlug;

  @Column(name = "room_id", nullable = false)
  private Long roomId;

  @Enumerated(EnumType.STRING)
  @Column(name = "device_type", nullable = false)
  private SmartDeviceType deviceType;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "external_id", nullable = false)
  private String externalId;

  @Column(name = "active", nullable = false)
  private boolean active;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  protected SmartDeviceEntity() {}

  public SmartDeviceEntity(String bnbSlug, Long roomId, SmartDeviceType deviceType, String name, String externalId, boolean active) {
    this.bnbSlug = bnbSlug;
    this.roomId = roomId;
    this.deviceType = deviceType;
    this.name = name;
    this.externalId = externalId;
    this.active = active;
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

  public SmartDeviceType getDeviceType() {
    return deviceType;
  }

  public String getName() {
    return name;
  }

  public String getExternalId() {
    return externalId;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
