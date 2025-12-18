package com.example.bnb.booking.api;

import com.example.bnb.booking.domain.Room;
import com.example.bnb.booking.service.BookingStore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rooms")
public class RoomsController {
  private final BookingStore store;

  public RoomsController(BookingStore store) {
    this.store = store;
  }

  public record CreateRoomRequest(
      @NotBlank String roomNumber,
      @NotBlank String smartLockId
  ) {}

  @PostMapping
  public Room create(@Valid @RequestBody CreateRoomRequest req) {
    return store.createRoom(req.roomNumber(), req.smartLockId());
  }

  @GetMapping
  public List<Room> list() {
    return store.listRooms();
  }

  @GetMapping("/{roomId}")
  public Room get(@PathVariable long roomId) {
    return store.getRoom(roomId);
  }
}
