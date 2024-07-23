package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemForRequestsResponse;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Requests {
    private Long id;

    private String description;

    private Long requestorId;

    private LocalDateTime created;

    private List<ItemForRequestsResponse> items;
}
