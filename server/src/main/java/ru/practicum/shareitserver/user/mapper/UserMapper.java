package ru.practicum.shareitserver.user.mapper;

import lombok.Generated;
import org.mapstruct.Mapper;
import ru.practicum.shareitserver.user.dto.User;
import ru.practicum.shareitserver.user.dto.UserDto;
import ru.practicum.shareitserver.user.dto.UserResponse;
import ru.practicum.shareitserver.user.entity.UserEntity;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Generated
@Mapper(componentModel = SPRING)
public interface UserMapper {

    User toUser(UserDto request);

    UserResponse toResponse(User user);

    UserEntity toEntity(User user);

    User toUser(UserEntity userEntity);
}
