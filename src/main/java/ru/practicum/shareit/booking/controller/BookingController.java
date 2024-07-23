package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;
    private final BookingMapper mapper;
    private final ItemService itemService;

    @PostMapping
    public BookingResponse create(@Valid @RequestBody BookingDto request, @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemService.checkItem(request.getItemId());
        Booking booking = mapper.toBooking(request, userId, BookingStatus.WAITING);
        BookingEntity bookingCreated = service.create(booking);
        return mapper.toBookingResponse(bookingCreated);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse approved(@PathVariable Long bookingId, @RequestParam("approved") boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingEntity bookingChanged = service.changeStatus(bookingId, approved, userId);
        return mapper.toBookingResponse(bookingChanged);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBookingById(@PathVariable Long bookingId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingEntity booking = service.getBookingById(bookingId, userId);
        return mapper.toBookingResponse(booking);
    }

    @GetMapping()
    public List<BookingResponse> getBookingsByUser(@RequestParam(name = "state", required = false, defaultValue = "ALL") String text,
                                          @RequestParam(name = "from", required = false) @PositiveOrZero Integer from,
                                                   @RequestParam(name = "size", required = false) @Positive Integer size,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<BookingEntity> bookings;
        if (from == null || size == null) {
            bookings = service.getBookingsByUser(text, userId);
        } else {
            bookings = service.getBookingsByUser(text, userId, from, size);
        }
        return mapper.toListBookingResponse(bookings);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getBookingsByOwner(@RequestParam(name = "state", required = false, defaultValue = "ALL") String text,
                                                    @RequestParam(name = "from", required = false) @PositiveOrZero Integer from,
                                                    @RequestParam(name = "size", required = false) @Positive Integer size,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<BookingEntity> bookings;
        if (from == null || size == null) {
            bookings = service.getBookingsByOwner(text, userId);
        } else {
            bookings = service.getBookingsByOwner(text, userId, from, size);
        }
        return mapper.toListBookingResponse(bookings);
    }


}
