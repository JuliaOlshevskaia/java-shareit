package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShortInfo;
import ru.practicum.shareit.item.dto.Comment;
import java.util.List;

@Data
@AllArgsConstructor
public class Item {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long userId;

    private BookingShortInfo lastBooking;

    private BookingShortInfo nextBooking;

    private List<Comment> comments;

}
