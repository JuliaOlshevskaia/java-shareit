package ru.practicum.shareitserver.booking.dto;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;

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
