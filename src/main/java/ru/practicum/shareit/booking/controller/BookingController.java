package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
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
import java.util.List;

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
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<BookingEntity> bookings = service.getBookingsByUser(text, userId);
        return mapper.toListBookingResponse(bookings);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getBookingsByOwner(@RequestParam(name = "state", required = false, defaultValue = "ALL") String text,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<BookingEntity> bookings = service.getBookingsByOwner(text, userId);
        return mapper.toListBookingResponse(bookings);
    }


}
