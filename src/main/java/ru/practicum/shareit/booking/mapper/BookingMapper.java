package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.Booking;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.entity.BookingEntity;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface BookingMapper {
    @Mapping(target = "bookerId", source = "userId")
    @Mapping(target = "status", source = "status")
    Booking toBooking(BookingDto request, Long userId, BookingStatus status);

    BookingDto toBookingDto(Booking booking);

    BookingEntity toEntity(Booking booking);

    BookingResponse toBookingResponse(BookingEntity bookingEntity);

    List<BookingResponse> toListBookingResponse(List<BookingEntity> bookingEntity);
}
