package ru.practicum.shareitserver.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;

@Generated
@Data
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String name;

    private String email;
}
