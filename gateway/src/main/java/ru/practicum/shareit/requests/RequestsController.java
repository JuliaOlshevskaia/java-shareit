package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.requests.dto.RequestsParamByPage;
import ru.practicum.shareit.requests.dto.RequestsDescription;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestsController {
    private final RequestsClient requestsClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody RequestsDescription requestsDescription,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestsClient.create(requestsDescription, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestsClient.getRequestByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable Long requestId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestsClient.getRequestById(requestId, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestByPage(@Valid RequestsParamByPage requestsParamByPage,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        if ((requestsParamByPage.getSize() != null && requestsParamByPage.getSize() <= 0) ||
                (requestsParamByPage.getFrom() != null && requestsParamByPage.getFrom() < 0)) {
            throw new ValidationException("Неверный размер запроса");
        }

        return requestsClient.getRequestByPage(requestsParamByPage.getFrom(), requestsParamByPage.getSize(), userId);
    }
}
