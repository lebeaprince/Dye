package com.example.bnb.booking.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<GuestEntity, Long> {
  List<GuestEntity> findByBnbSlugOrderByIdAsc(String bnbSlug);

  Optional<GuestEntity> findByIdAndBnbSlug(Long id, String bnbSlug);
}
