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
@Table(name = "guests")
public class GuestEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "bnb_slug", nullable = false)
  private String bnbSlug;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(name = "phone_number", nullable = false)
  private String phoneNumber;

  @Column(name = "sms_opt_in", nullable = false)
  private boolean smsOptIn;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  protected GuestEntity() {}

  public GuestEntity(String bnbSlug, String fullName, String phoneNumber, boolean smsOptIn) {
    this.bnbSlug = bnbSlug;
    this.fullName = fullName;
    this.phoneNumber = phoneNumber;
    this.smsOptIn = smsOptIn;
  }

  public Long getId() {
    return id;
  }

  public String getBnbSlug() {
    return bnbSlug;
  }

  public String getFullName() {
    return fullName;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public boolean isSmsOptIn() {
    return smsOptIn;
  }
}
