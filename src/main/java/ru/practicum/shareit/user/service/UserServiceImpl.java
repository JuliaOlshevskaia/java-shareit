package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    private void validate(User user) {
        if (user.getName() == null) {
            throw new ValidationException("Поле name пустое");
        }
        if (user.getEmail() == null) {
            throw new ValidationException("Поле email пустое");
        }
    }

    @Override
    public User create(User user) {
        validate(user);
        UserEntity userEntity = mapper.toEntity(user);
        UserEntity userCreated = userRepository.save(userEntity);
        return mapper.toUser(userCreated);
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User update(Long userId, User user) {
        if (userRepository.existsById(userId)) {
            UserEntity userOld = userRepository.findById(userId).get();
            User newUser = new User(userId, user.getName() == null ? userOld.getName() : user.getName(),
                    user.getEmail() == null ? userOld.getEmail() : user.getEmail());
            UserEntity userNewEntity = mapper.toEntity(newUser);
            userRepository.save(userNewEntity);
        }
        return mapper.toUser(userRepository.findById(userId).get());
    }

    @Override
    public List<User> getAllUsers() {
        List<UserEntity> allUsers = userRepository.findAll();
        return allUsers.stream().map(mapper::toUser).collect(Collectors.toList());
    }

    @Override
    public User getUserById(Long userId) {
        checkUser(userId);
        UserEntity userEntity = userRepository.findById(userId).get();
        return mapper.toUser(userEntity);
    }

    @Override
    public void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataNotFoundException("Пользователя с id=" + userId + " не существует");
        }
    }
}
