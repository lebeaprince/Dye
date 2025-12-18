package com.example.bnb.booking.service;

import com.example.bnb.booking.domain.Booking;
import com.example.bnb.booking.domain.BookingStatus;
import com.example.bnb.booking.domain.Guest;
import com.example.bnb.booking.domain.Room;
import com.example.bnb.booking.domain.ServicePackage;
import com.example.bnb.booking.domain.SmartDevice;
import com.example.bnb.booking.domain.SmartDeviceType;
import com.example.bnb.booking.domain.StayDurationOption;
import com.example.bnb.booking.persistence.BookingEntity;
import com.example.bnb.booking.persistence.BookingRepository;
import com.example.bnb.booking.persistence.GuestEntity;
import com.example.bnb.booking.persistence.GuestRepository;
import com.example.bnb.booking.persistence.RoomEntity;
import com.example.bnb.booking.persistence.RoomRepository;
import com.example.bnb.booking.persistence.SmartDeviceEntity;
import com.example.bnb.booking.persistence.SmartDeviceRepository;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingStore {
  private static final String DEFAULT_BNB_SLUG = "default";

  private final RoomRepository rooms;
  private final GuestRepository guests;
  private final BookingRepository bookings;
  private final SmartDeviceRepository devices;

  public BookingStore(RoomRepository rooms, GuestRepository guests, BookingRepository bookings, SmartDeviceRepository devices) {
    this.rooms = rooms;
    this.guests = guests;
    this.bookings = bookings;
    this.devices = devices;
  }

  public static String normalizeBnbSlug(String bnbSlug) {
    if (bnbSlug == null || bnbSlug.isBlank()) {
      return DEFAULT_BNB_SLUG;
    }
    return bnbSlug.trim();
  }

  @Transactional
  public Room createRoom(String bnbSlug, String roomNumber, String smartLockId) {
    RoomEntity saved = rooms.save(new RoomEntity(normalizeBnbSlug(bnbSlug), roomNumber, smartLockId, true));
    return toRoom(saved);
  }

  @Transactional(readOnly = true)
  public List<Room> listRooms(String bnbSlug) {
    return rooms.findByBnbSlugOrderByIdAsc(normalizeBnbSlug(bnbSlug)).stream().map(BookingStore::toRoom).toList();
  }

  @Transactional(readOnly = true)
  public Room getRoom(String bnbSlug, long roomId) {
    RoomEntity room = rooms.findByIdAndBnbSlug(roomId, normalizeBnbSlug(bnbSlug))
        .orElseThrow(() -> new NoSuchElementException("Room not found: " + roomId));
    return toRoom(room);
  }

  @Transactional
  public Guest createGuest(String bnbSlug, String fullName, String phoneNumber, boolean smsOptIn) {
    GuestEntity saved = guests.save(new GuestEntity(normalizeBnbSlug(bnbSlug), fullName, phoneNumber, smsOptIn));
    return toGuest(saved);
  }

  @Transactional(readOnly = true)
  public List<Guest> listGuests(String bnbSlug) {
    return guests.findByBnbSlugOrderByIdAsc(normalizeBnbSlug(bnbSlug)).stream().map(BookingStore::toGuest).toList();
  }

  @Transactional(readOnly = true)
  public Guest getGuest(String bnbSlug, long guestId) {
    GuestEntity guest = guests.findByIdAndBnbSlug(guestId, normalizeBnbSlug(bnbSlug))
        .orElseThrow(() -> new NoSuchElementException("Guest not found: " + guestId));
    return toGuest(guest);
  }

  @Transactional
  public Booking createBooking(
      String bnbSlug,
      long roomId,
      long guestId,
      OffsetDateTime startAt,
      StayDurationOption stayDuration,
      int durationUnits,
      ServicePackage servicePackage
  ) {
    String slug = normalizeBnbSlug(bnbSlug);
    rooms.findByIdAndBnbSlug(roomId, slug).orElseThrow(() -> new NoSuchElementException("Room not found: " + roomId));
    guests.findByIdAndBnbSlug(guestId, slug).orElseThrow(() -> new NoSuchElementException("Guest not found: " + guestId));

    OffsetDateTime endAt = computeEnd(startAt, stayDuration, durationUnits);

    BookingEntity saved = bookings.save(new BookingEntity(
        slug,
        roomId,
        guestId,
        startAt,
        endAt,
        stayDuration,
        durationUnits,
        servicePackage,
        BookingStatus.PENDING_PAYMENT
    ));

    return toBooking(saved);
  }

  @Transactional(readOnly = true)
  public Booking getBooking(String bnbSlug, long bookingId) {
    BookingEntity booking = bookings.findByIdAndBnbSlug(bookingId, normalizeBnbSlug(bnbSlug))
        .orElseThrow(() -> new NoSuchElementException("Booking not found: " + bookingId));
    return toBooking(booking);
  }

  @Transactional
  public Booking cancelBooking(String bnbSlug, long bookingId) {
    BookingEntity booking = bookings.findByIdAndBnbSlug(bookingId, normalizeBnbSlug(bnbSlug))
        .orElseThrow(() -> new NoSuchElementException("Booking not found: " + bookingId));

    if (booking.getStatus() != BookingStatus.CANCELLED) {
      booking.setStatus(BookingStatus.CANCELLED);
      booking = bookings.save(booking);
    }

    return toBooking(booking);
  }

  @Transactional
  public Booking confirmBooking(String bnbSlug, long bookingId) {
    BookingEntity booking = bookings.findByIdAndBnbSlug(bookingId, normalizeBnbSlug(bnbSlug))
        .orElseThrow(() -> new NoSuchElementException("Booking not found: " + bookingId));

    if (booking.getStatus() == BookingStatus.CANCELLED) {
      throw new IllegalStateException("Cannot confirm a cancelled booking");
    }

    if (booking.getStatus() != BookingStatus.CONFIRMED) {
      booking.setStatus(BookingStatus.CONFIRMED);
      booking = bookings.save(booking);
    }

    return toBooking(booking);
  }

  @Transactional
  public SmartDevice addDevice(String bnbSlug, long roomId, SmartDeviceType deviceType, String name, String externalId) {
    String slug = normalizeBnbSlug(bnbSlug);
    rooms.findByIdAndBnbSlug(roomId, slug).orElseThrow(() -> new NoSuchElementException("Room not found: " + roomId));
    SmartDeviceEntity saved = devices.save(new SmartDeviceEntity(slug, roomId, deviceType, name, externalId, true));
    return toDevice(saved);
  }

  @Transactional(readOnly = true)
  public List<SmartDevice> listDevices(String bnbSlug, Long roomId) {
    String slug = normalizeBnbSlug(bnbSlug);
    List<SmartDeviceEntity> found = (roomId == null)
        ? devices.findByBnbSlugOrderByIdAsc(slug)
        : devices.findByBnbSlugAndRoomIdOrderByIdAsc(slug, roomId);
    return found.stream().map(BookingStore::toDevice).toList();
  }

  @Transactional
  public SmartDevice removeDevice(String bnbSlug, long deviceId) {
    SmartDeviceEntity existing = devices.findByIdAndBnbSlug(deviceId, normalizeBnbSlug(bnbSlug))
        .orElseThrow(() -> new NoSuchElementException("Smart device not found: " + deviceId));
    devices.delete(existing);
    return toDevice(existing);
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

  private static Room toRoom(RoomEntity e) {
    return new Room(e.getId(), e.getRoomNumber(), e.getSmartLockId(), e.isActive());
  }

  private static Guest toGuest(GuestEntity e) {
    return new Guest(e.getId(), e.getFullName(), e.getPhoneNumber(), e.isSmsOptIn());
  }

  private static Booking toBooking(BookingEntity e) {
    return new Booking(
        e.getId(),
        e.getRoomId(),
        e.getGuestId(),
        e.getStartAt(),
        e.getEndAt(),
        e.getStayDuration(),
        e.getDurationUnits(),
        e.getServicePackage(),
        e.getStatus()
    );
  }

  private static SmartDevice toDevice(SmartDeviceEntity e) {
    return new SmartDevice(e.getId(), e.getRoomId(), e.getDeviceType(), e.getName(), e.getExternalId(), e.isActive());
  }
}
