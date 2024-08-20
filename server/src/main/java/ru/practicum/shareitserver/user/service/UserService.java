package ru.practicum.shareitserver.user.service;

import ru.practicum.shareitserver.exceptions.ValidationException;
import ru.practicum.shareitserver.user.dto.User;

import java.util.List;

public interface UserService {

    User create(User user) throws ValidationException;

    void delete(Long userId);

    User update(Long userId, User user);

    List<User> getAllUsers();

    User getUserById(Long userId);

    void checkUser(Long userId);
}
