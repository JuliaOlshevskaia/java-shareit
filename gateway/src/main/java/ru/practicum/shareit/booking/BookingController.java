package ru.practicum.shareit.booking;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody BookingDto request, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.create(userId, request);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approved(@PathVariable Long bookingId, @RequestParam("approved") boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.approved(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getBookingsByUser(@RequestParam(name = "state", required = false, defaultValue = "ALL") String text,
                                          @RequestParam(name = "from", required = false) @PositiveOrZero Integer from,
                                                   @RequestParam(name = "size", required = false) @Positive Integer size,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getBookingsByUser(text, from, size, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestParam(name = "state", required = false, defaultValue = "ALL") String text,
                                                    @RequestParam(name = "from", required = false) @PositiveOrZero Integer from,
                                                    @RequestParam(name = "size", required = false) @Positive Integer size,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getBookingsByOwner(text, from, size, userId);
    }


}
