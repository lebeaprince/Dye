package com.example.bnb.booking.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "rooms")
public class RoomEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "bnb_slug", nullable = false)
  private String bnbSlug;

  @Column(name = "room_number", nullable = false)
  private String roomNumber;

  @Column(name = "smart_lock_id", nullable = false)
  private String smartLockId;

  @Column(name = "active", nullable = false)
  private boolean active;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  protected RoomEntity() {}

  public RoomEntity(String bnbSlug, String roomNumber, String smartLockId, boolean active) {
    this.bnbSlug = bnbSlug;
    this.roomNumber = roomNumber;
    this.smartLockId = smartLockId;
    this.active = active;
  }

  public Long getId() {
    return id;
  }

  public String getBnbSlug() {
    return bnbSlug;
  }

  public String getRoomNumber() {
    return roomNumber;
  }

  public String getSmartLockId() {
    return smartLockId;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
