package ru.practicum.shareitserver.booking.mapper;

import lombok.Generated;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareitserver.booking.dto.Booking;
import ru.practicum.shareitserver.booking.dto.BookingResponse;
import ru.practicum.shareitserver.booking.enums.BookingStatus;
import ru.practicum.shareitserver.booking.dto.BookingDto;
import ru.practicum.shareitserver.booking.entity.BookingEntity;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Generated
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
