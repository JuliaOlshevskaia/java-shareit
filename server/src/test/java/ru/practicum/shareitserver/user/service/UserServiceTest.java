package ru.practicum.shareitserver.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareitserver.exceptions.DataNotFoundException;
import ru.practicum.shareitserver.exceptions.ValidationException;
import ru.practicum.shareitserver.user.dto.User;
import ru.practicum.shareitserver.user.entity.UserEntity;
import ru.practicum.shareitserver.user.mapper.UserMapper;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserRepository repository;
    private UserServiceImpl service;
    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
        mapper = Mappers.getMapper(UserMapper.class);
        service = new UserServiceImpl(repository, mapper);
    }

    @Test
    void create() {
        var user = new User(null, "Name1", "mail1@yandex.ru");
        var userEntity = new UserEntity();
        userEntity.setName("Name1");
        userEntity.setEmail("mail1@yandex.ru");
        var userWithId = new UserEntity();
        userWithId.setId(1L);
        userWithId.setName("Name1");
        userWithId.setEmail("mail1@yandex.ru");
        when(repository.save(any())).thenReturn(userWithId);

        var result = service.create(user);
        assertNotNull(result);
        assertEquals(userWithId.getId(), result.getId());
    }

    @Test
    void creatNullNameThrowException() {
        var user = new User(null, null, "mail1@yandex.ru");
        var userWithId = new UserEntity();
        userWithId.setId(1L);
        userWithId.setEmail("mail1@yandex.ru");
        when(repository.save(any())).thenReturn(userWithId);

        assertThrows(ValidationException.class, () -> service.create(user));
    }

    @Test
    void creatNullEmailThrowException() {
        var user = new User(null, "Name1", null);
        var userWithId = new UserEntity();
        userWithId.setId(1L);
        userWithId.setName("Name1");
        when(repository.save(any())).thenReturn(userWithId);

        assertThrows(ValidationException.class, () -> service.create(user));
    }

    @Test
    void getAllUsers() {
        var user = new UserEntity();
        user.setId(1L);
        user.setName("Name1");
        user.setEmail("mail1@yandex.ru");
        when(repository.findAll()).thenReturn(Collections.singletonList(user));

        var result = service.getAllUsers();
        assertNotNull(result);
        assertEquals(user.getId(), result.get(0).getId());
    }

    @Test
    void delete() {
        var user1 = new User(1L, "Name1", "mail1@yandex.ru");
        service.create(user1);

        var user2 = new User(2L, "Name2", "mail2@yandex.ru");
        var userEntity2 = new UserEntity();
        userEntity2.setId(2L);
        userEntity2.setName("Name2");
        userEntity2.setEmail("mail2@yandex.ru");
        service.create(user2);

        when(repository.findAll()).thenReturn(Collections.singletonList(userEntity2));

        service.delete(user1.getId());

        var result = service.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user2.getId(), result.get(0).getId());
    }

    @Test
    void update() {
        var userEntityUpdated = new UserEntity();
        userEntityUpdated.setId(1L);
        userEntityUpdated.setName("Name New");

        var userEntityAfterUpdated = new UserEntity();
        userEntityAfterUpdated.setId(1L);
        userEntityAfterUpdated.setName("Name New");
        userEntityAfterUpdated.setEmail("mail1@yandex.ru");

        when(repository.save(any())).thenReturn(userEntityAfterUpdated);
        when(repository.existsById(anyLong())).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(userEntityAfterUpdated));

        var result = service.update(1L, mapper.toUser(userEntityUpdated));
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Name New", result.getName());

    }

    @Test
    void getUserById() {
        var user = new UserEntity();
        user.setId(1L);
        user.setName("Name1");
        user.setEmail("mail1@yandex.ru");

        when(repository.findById(any())).thenReturn(Optional.of(user));
        when(repository.existsById(1L)).thenReturn(true);

        var result = service.getUserById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Name1", result.getName());
        assertEquals("mail1@yandex.ru", result.getEmail());
    }

    @Test
    void checkUser() {
        when(repository.existsById(any())).thenReturn(false);

        assertThrows(DataNotFoundException.class, () -> service.checkUser(1L));
    }
}
