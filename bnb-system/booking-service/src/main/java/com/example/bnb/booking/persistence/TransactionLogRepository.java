package com.example.bnb.booking.persistence;

import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionLogRepository extends JpaRepository<TransactionLogEntity, Long> {
  @Query("""
      select t from TransactionLogEntity t
      where t.bnbSlug = :bnbSlug
        and (:roomId is null or t.roomId = :roomId)
        and (:deviceId is null or t.deviceId = :deviceId)
        and (:bookingId is null or t.bookingId = :bookingId)
        and (:guestId is null or t.guestId = :guestId)
        and (:from is null or t.createdAt >= :from)
        and (:to is null or t.createdAt <= :to)
      order by t.createdAt desc, t.id desc
      """)
  List<TransactionLogEntity> search(
      @Param("bnbSlug") String bnbSlug,
      @Param("roomId") Long roomId,
      @Param("deviceId") Long deviceId,
      @Param("bookingId") Long bookingId,
      @Param("guestId") Long guestId,
      @Param("from") OffsetDateTime from,
      @Param("to") OffsetDateTime to,
      Pageable pageable
  );

  interface ActionCount {
    String getAction();
    long getCount();
  }

  @Query("""
      select t.action as action, count(t) as count
      from TransactionLogEntity t
      where t.bnbSlug = :bnbSlug
        and t.createdAt >= :from
      group by t.action
      order by count(t) desc
      """)
  List<ActionCount> countByActionSince(
      @Param("bnbSlug") String bnbSlug,
      @Param("from") OffsetDateTime from
  );
}

