package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.Booking;
import ru.practicum.shareit.booking.entity.BookingEntity;

import java.util.List;

public interface BookingService {
    BookingEntity create(Booking booking);
    BookingEntity changeStatus(Long bookingId,  boolean approved, Long userId);
    BookingEntity getBookingById(Long bookingId, Long userId);
    List<BookingEntity> getBookingsByUser(String text, Long userId);
    List<BookingEntity> getBookingsByOwner(String text, Long userId);
}
