package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;

@Generated
@Data
@AllArgsConstructor
public class ItemUpdateDto {

    private String name;

    private String description;

    private Boolean available;
}
