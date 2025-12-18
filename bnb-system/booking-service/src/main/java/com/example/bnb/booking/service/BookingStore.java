package com.example.bnb.booking.service;

import com.example.bnb.booking.domain.Booking;
import com.example.bnb.booking.domain.BookingStatus;
import com.example.bnb.booking.domain.Guest;
import com.example.bnb.booking.domain.Room;
import com.example.bnb.booking.domain.ServicePackage;
import com.example.bnb.booking.domain.StayDurationOption;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class BookingStore {
  private final AtomicLong roomIdSeq = new AtomicLong(1000);
  private final AtomicLong guestIdSeq = new AtomicLong(2000);
  private final AtomicLong bookingIdSeq = new AtomicLong(3000);

  private final ConcurrentMap<Long, Room> rooms = new ConcurrentHashMap<>();
  private final ConcurrentMap<Long, Guest> guests = new ConcurrentHashMap<>();
  private final ConcurrentMap<Long, Booking> bookings = new ConcurrentHashMap<>();

  public Room createRoom(String roomNumber, String smartLockId) {
    long id = roomIdSeq.incrementAndGet();
    Room room = new Room(id, roomNumber, smartLockId, true);
    rooms.put(id, room);
    return room;
  }

  public List<Room> listRooms() {
    return new ArrayList<>(rooms.values());
  }

  public Room getRoom(long roomId) {
    Room room = rooms.get(roomId);
    if (room == null) {
      throw new NoSuchElementException("Room not found: " + roomId);
    }
    return room;
  }

  public Guest createGuest(String fullName, String phoneNumber, boolean smsOptIn) {
    long id = guestIdSeq.incrementAndGet();
    Guest guest = new Guest(id, fullName, phoneNumber, smsOptIn);
    guests.put(id, guest);
    return guest;
  }

  public List<Guest> listGuests() {
    return new ArrayList<>(guests.values());
  }

  public Guest getGuest(long guestId) {
    Guest guest = guests.get(guestId);
    if (guest == null) {
      throw new NoSuchElementException("Guest not found: " + guestId);
    }
    return guest;
  }

  public Booking createBooking(
      long roomId,
      long guestId,
      OffsetDateTime startAt,
      StayDurationOption stayDuration,
      int durationUnits,
      ServicePackage servicePackage
  ) {
    if (!rooms.containsKey(roomId)) {
      throw new NoSuchElementException("Room not found: " + roomId);
    }
    if (!guests.containsKey(guestId)) {
      throw new NoSuchElementException("Guest not found: " + guestId);
    }

    OffsetDateTime endAt = computeEnd(startAt, stayDuration, durationUnits);

    long id = bookingIdSeq.incrementAndGet();
    Booking booking = new Booking(
        id,
        roomId,
        guestId,
        startAt,
        endAt,
        stayDuration,
        durationUnits,
        servicePackage,
        BookingStatus.PENDING_PAYMENT
    );
    bookings.put(id, booking);
    return booking;
  }

  public Booking getBooking(long bookingId) {
    Booking booking = bookings.get(bookingId);
    if (booking == null) {
      throw new NoSuchElementException("Booking not found: " + bookingId);
    }
    return booking;
  }

  public Booking cancelBooking(long bookingId) {
    return bookings.compute(bookingId, (id, existing) -> {
      if (existing == null) {
        throw new NoSuchElementException("Booking not found: " + bookingId);
      }
      if (existing.status() == BookingStatus.CANCELLED) {
        return existing;
      }
      return new Booking(
          existing.id(),
          existing.roomId(),
          existing.guestId(),
          existing.startAt(),
          existing.endAt(),
          existing.stayDuration(),
          existing.durationUnits(),
          existing.servicePackage(),
          BookingStatus.CANCELLED
      );
    });
  }

  public Booking confirmBooking(long bookingId) {
    return bookings.compute(bookingId, (id, existing) -> {
      if (existing == null) {
        throw new NoSuchElementException("Booking not found: " + bookingId);
      }
      if (existing.status() == BookingStatus.CANCELLED) {
        throw new IllegalStateException("Cannot confirm a cancelled booking");
      }
      return new Booking(
          existing.id(),
          existing.roomId(),
          existing.guestId(),
          existing.startAt(),
          existing.endAt(),
          existing.stayDuration(),
          existing.durationUnits(),
          existing.servicePackage(),
          BookingStatus.CONFIRMED
      );
    });
  }

  private static OffsetDateTime computeEnd(OffsetDateTime startAt, StayDurationOption option, int durationUnits) {
    if (durationUnits <= 0) {
      throw new IllegalArgumentException("durationUnits must be > 0");
    }
    return switch (option) {
      case HOURLY -> startAt.plusHours(durationUnits);
      case NIGHTLY -> startAt.plusDays(durationUnits);
      case WEEKLY -> startAt.plusWeeks(durationUnits);
      case MONTHLY -> startAt.plusMonths(durationUnits);
    };
  }
}
