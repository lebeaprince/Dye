package com.example.bnb.booking.api;

import com.example.bnb.booking.domain.Room;
import com.example.bnb.booking.service.BookingStore;
import com.example.bnb.booking.service.TransactionLogger;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rooms")
public class RoomsController {
  private final BookingStore store;
  private final TransactionLogger tx;

  public RoomsController(BookingStore store, TransactionLogger tx) {
    this.store = store;
    this.tx = tx;
  }

  public record CreateRoomRequest(
      @NotBlank String roomNumber,
      @NotBlank String smartLockId
  ) {}

  @PostMapping
  public Room create(
      @RequestHeader(value = "X-Bnb-Slug", required = false) String bnbSlug,
      @RequestHeader(value = "X-Actor", required = false) String actor,
      @Valid @RequestBody CreateRoomRequest req
  ) {
    String slug = BookingStore.normalizeBnbSlug(bnbSlug);
    Room room = store.createRoom(slug, req.roomNumber(), req.smartLockId());
    tx.record(
        slug,
        actor,
        "ROOM_CREATED",
        "Created room " + room.roomNumber(),
        room.id(),
        null,
        null,
        null
    );
    return room;
  }

  @GetMapping
  public List<Room> list(@RequestHeader(value = "X-Bnb-Slug", required = false) String bnbSlug) {
    return store.listRooms(BookingStore.normalizeBnbSlug(bnbSlug));
  }

  @GetMapping("/{roomId}")
  public Room get(
      @RequestHeader(value = "X-Bnb-Slug", required = false) String bnbSlug,
      @PathVariable long roomId
  ) {
    return store.getRoom(BookingStore.normalizeBnbSlug(bnbSlug), roomId);
  }
}
