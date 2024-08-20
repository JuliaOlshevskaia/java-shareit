package ru.practicum.shareitserver.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;

import java.time.LocalDateTime;

@Generated
@Data
@AllArgsConstructor
public class BookingDto {

    private Long itemId;

    private LocalDateTime start;

    private LocalDateTime end;
}
