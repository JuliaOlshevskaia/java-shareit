package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShortInfo;

import java.util.List;

@Data
public class ItemResponse {

    private Long id;

    private String name;

    private String description;

    private boolean available;

    private BookingShortInfo lastBooking;

    private BookingShortInfo nextBooking;

    private List<Comment> comments;
}
