package com.example.bnb.booking.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmartDeviceRepository extends JpaRepository<SmartDeviceEntity, Long> {
  List<SmartDeviceEntity> findByBnbSlugOrderByIdAsc(String bnbSlug);

  List<SmartDeviceEntity> findByBnbSlugAndRoomIdOrderByIdAsc(String bnbSlug, Long roomId);

  Optional<SmartDeviceEntity> findByIdAndBnbSlug(Long id, String bnbSlug);
}
