package ru.practicum.shareitserver.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import ru.practicum.shareitserver.booking.enums.BookingStatus;

import java.time.LocalDateTime;

@Generated
@Data
@AllArgsConstructor
public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private BookingStatus status;
}
