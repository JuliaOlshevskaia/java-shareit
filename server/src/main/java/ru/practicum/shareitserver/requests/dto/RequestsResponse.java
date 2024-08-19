package ru.practicum.shareitserver.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import ru.practicum.shareitserver.item.dto.ItemForRequestsResponse;

import java.time.LocalDateTime;
import java.util.List;

@Generated
@Data
@AllArgsConstructor
public class RequestsResponse {
    private Long id;

    private String description;

    private LocalDateTime created;

    private List<ItemForRequestsResponse> items;
}
