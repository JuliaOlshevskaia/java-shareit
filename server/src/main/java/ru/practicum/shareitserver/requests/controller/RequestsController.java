package ru.practicum.shareitserver.requests.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitserver.exceptions.ValidationException;
import ru.practicum.shareitserver.requests.dto.RequestsParamByPage;
import ru.practicum.shareitserver.requests.dto.Requests;
import ru.practicum.shareitserver.requests.dto.RequestsDescription;
import ru.practicum.shareitserver.requests.dto.RequestsResponse;
import ru.practicum.shareitserver.requests.mapper.RequestsMapper;
import ru.practicum.shareitserver.requests.service.RequestsService;
import ru.practicum.shareitserver.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestsController {
    private final RequestsService service;
    private final RequestsMapper mapper;
    private final UserService userService;

    @PostMapping
    public RequestsResponse create(@RequestBody RequestsDescription requestsDescription,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        userService.checkUser(userId);
        String description = requestsDescription.getDescription();
        Requests requests = mapper.toRequests(description, userId, LocalDateTime.now());
        Requests requestsSaved = service.create(requests);
        return mapper.toResponse(requestsSaved);
    }

    @GetMapping
    public List<RequestsResponse> getRequestByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        userService.checkUser(userId);
        List<Requests> requests = service.getRequestsByUser(userId);
        return mapper.toListResponse(requests);
    }

    @GetMapping("/{requestId}")
    public RequestsResponse getRequestById(@PathVariable Long requestId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        userService.checkUser(userId);
        service.checkRequests(requestId);
        Requests requests = service.getRequestsById(requestId);
        return mapper.toResponse(requests);
    }

    @GetMapping("/all")
    public List<RequestsResponse> getRequestByPage(RequestsParamByPage requestsParamByPage,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        if ((requestsParamByPage.getSize() != null && requestsParamByPage.getSize() <= 0) ||
                (requestsParamByPage.getFrom() != null && requestsParamByPage.getFrom() < 0)) {
            throw new ValidationException("Неверный размер запроса");
        }
        userService.checkUser(userId);
        List<RequestsResponse> response = new ArrayList<>();
        if (requestsParamByPage.getSize() != null) {
            List<Requests> requests = service.getRequestsByPage(requestsParamByPage.getFrom(), requestsParamByPage.getSize(), userId);
            response = mapper.toListResponse(requests);
        }
        return response;
    }
}
