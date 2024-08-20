package ru.practicum.shareitserver.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import ru.practicum.shareitserver.booking.dto.BookingShortInfo;

import java.util.List;

@Generated
@Data
@AllArgsConstructor
public class ItemResponse {

    private Long id;

    private String name;

    private String description;

    private boolean available;

    private BookingShortInfo lastBooking;

    private BookingShortInfo nextBooking;

    private List<Comment> comments;

    private Long requestId;
}
