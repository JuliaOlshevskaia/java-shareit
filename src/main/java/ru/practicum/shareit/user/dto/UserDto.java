package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;

@Getter
@Validated
@AllArgsConstructor
public class UserDto {

    private final Long id;

    private final String name;

    @Email
    private final String email;
}
