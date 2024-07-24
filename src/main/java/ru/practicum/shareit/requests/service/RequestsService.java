package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.Requests;

import java.util.List;

public interface RequestsService {

    Requests create(Requests requests);

    List<Requests> getRequestsByUser(Long userId);

    Requests getRequestsById(Long requestsId);

    List<Requests> getRequestsByPage(Integer from, Integer size, Long userId);

    void checkRequests(Long requestsId);
}
