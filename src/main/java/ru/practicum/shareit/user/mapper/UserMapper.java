package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto request);

    UserResponse toResponse(User user);
}
