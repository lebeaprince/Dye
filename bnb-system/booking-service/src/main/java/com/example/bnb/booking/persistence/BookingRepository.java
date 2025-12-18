package com.example.bnb.booking.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
  Optional<BookingEntity> findByIdAndBnbSlug(Long id, String bnbSlug);
}
