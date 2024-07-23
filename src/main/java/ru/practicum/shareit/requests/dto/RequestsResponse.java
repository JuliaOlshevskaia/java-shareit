package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForRequestsResponse;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class RequestsResponse {
    private Long id;

    private String description;

    private LocalDateTime created;

    private List<ItemForRequestsResponse> items;
}
