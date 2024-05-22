package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.User;

import java.util.List;

public interface UserService {

    User create(User user) throws ValidationException;

    void delete(Long userId);

    User update(Long userId, User user);

    List<User> getAllUsers();

    User getUserById(Long userId);

    void checkUser(Long userId);
}
