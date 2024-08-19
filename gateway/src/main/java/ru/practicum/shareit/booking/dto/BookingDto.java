package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Generated
@Data
@AllArgsConstructor
public class BookingDto {
    private Long itemId;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
}
