package ru.practicum.shareitserver.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitserver.booking.dto.Booking;
import ru.practicum.shareitserver.booking.dto.BookingDto;
import ru.practicum.shareitserver.booking.dto.BookingResponse;
import ru.practicum.shareitserver.booking.entity.BookingEntity;
import ru.practicum.shareitserver.booking.enums.BookingStatus;
import ru.practicum.shareitserver.booking.mapper.BookingMapper;
import ru.practicum.shareitserver.booking.service.BookingService;
import ru.practicum.shareitserver.item.service.ItemService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;
    private final BookingMapper mapper;
    private final ItemService itemService;

    @PostMapping
    public BookingResponse create(@RequestBody BookingDto request, @RequestHeader("X-Sharer-User-Id") Long userId) {
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
                                          @RequestParam(name = "from", required = false) Integer from,
                                                   @RequestParam(name = "size", required = false) Integer size,
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
                                                    @RequestParam(name = "from", required = false) Integer from,
                                                    @RequestParam(name = "size", required = false) Integer size,
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
