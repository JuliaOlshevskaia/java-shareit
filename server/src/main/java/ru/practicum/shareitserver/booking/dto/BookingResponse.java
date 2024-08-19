package ru.practicum.shareitserver.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import ru.practicum.shareitserver.booking.enums.BookingStatus;
import ru.practicum.shareitserver.item.dto.Item;
import ru.practicum.shareitserver.user.dto.User;

import java.time.LocalDateTime;

@Generated
@Data
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;
}
