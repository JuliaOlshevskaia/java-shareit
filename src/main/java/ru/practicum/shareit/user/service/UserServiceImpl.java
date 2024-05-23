package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.EmailDublicateException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private Map<Long, User> users = new HashMap<>();
    private Long generatedId = 0L;

    @Override
    public User create(User user) {
        validate(user);
        checkDublicateEmail(user.getEmail());
        ++generatedId;
        user.setId(generatedId);
        users.put(generatedId, user);
        return users.get(generatedId);
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }

    @Override
    public User update(Long userId, User user) {
        if (users.containsKey(userId)) {
            checkDublicateEmail(user, userId);
            User userOld = users.get(userId);
            User newUser = new User(userId, user.getName() == null ? userOld.getName() : user.getName(),
                    user.getEmail() == null ? userOld.getEmail() : user.getEmail());
            users.put(userId, newUser);
        }
        return users.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }


    private void checkDublicateEmail(String emailNew) {
       users.values().stream().filter(f -> f.getEmail().equals(emailNew))
                .forEach(a -> {
                    throw new EmailDublicateException("Пользователь с таким email уже существует: " + emailNew);
                });
    }

    private void validate(User user) {
        if (user.getName() == null) {
            throw new ValidationException("Поле name пустое");
        }
        if (user.getEmail() == null) {
            throw new ValidationException("Поле email пустое");
        }
    }

    private void checkDublicateEmail(User userNew, Long userId) {
        users.values().stream().filter(f ->
                (f.getEmail().equals(userNew.getEmail()) && !(f.getId().equals(userId))) && !(userNew.getEmail() == null))
                .forEach(a -> {
                    throw new EmailDublicateException("Пользователь с таким email уже существует: " + userNew.getEmail());
                });
    }

    @Override
    public void checkUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new DataNotFoundException("Пользователя с id=" + userId + " не существует");
        }
    }
}
