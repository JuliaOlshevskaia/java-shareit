package ru.practicum.shareitserver.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;

@Generated
@Data
@AllArgsConstructor
public class ItemForRequestsResponse {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}
