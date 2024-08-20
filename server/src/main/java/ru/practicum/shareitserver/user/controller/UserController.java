package ru.practicum.shareitserver.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitserver.user.dto.User;
import ru.practicum.shareitserver.user.dto.UserDto;
import ru.practicum.shareitserver.user.dto.UserResponse;
import ru.practicum.shareitserver.user.mapper.UserMapper;
import ru.practicum.shareitserver.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    @PostMapping
    public UserResponse create(@RequestBody UserDto request) {
        User user = mapper.toUser(request);
        User modified = service.create(user);
        return mapper.toResponse(modified);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        service.delete(userId);
    }

    @PatchMapping("/{userId}")
    public UserResponse update(@RequestBody UserDto request, @PathVariable Long userId) {
        User user = mapper.toUser(request);
        User modified = service.update(userId, user);
        return mapper.toResponse(modified);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        return service.getUserById(userId);
    }
}
