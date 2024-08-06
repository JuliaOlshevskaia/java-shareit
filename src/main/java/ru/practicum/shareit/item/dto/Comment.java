package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;

import java.time.LocalDateTime;

@Generated
@Data
@AllArgsConstructor
public class Comment {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
