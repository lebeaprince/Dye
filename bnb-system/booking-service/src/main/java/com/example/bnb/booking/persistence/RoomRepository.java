package com.example.bnb.booking.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
  List<RoomEntity> findByBnbSlugOrderByIdAsc(String bnbSlug);

  Optional<RoomEntity> findByIdAndBnbSlug(Long id, String bnbSlug);
}
