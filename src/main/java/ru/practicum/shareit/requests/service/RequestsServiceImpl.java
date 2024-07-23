package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestsResponse;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.Requests;
import ru.practicum.shareit.requests.entity.RequestsEntity;
import ru.practicum.shareit.requests.mapper.RequestsMapper;
import ru.practicum.shareit.requests.repository.RequestsRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RequestsServiceImpl implements RequestsService{
    private final RequestsRepository requestsRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestsMapper mapper;
    private final ItemMapper itemMapper;

    @Override
    public Requests create(Requests requests) {
        UserEntity user = userRepository.findById(requests.getRequestorId()).get();
        RequestsEntity requestsEntity = mapper.toEntity(requests);
        requestsEntity.setRequestor(user);
        RequestsEntity requestsSaved = requestsRepository.save(requestsEntity);
        return mapper.toRequests(requestsSaved);
    }

    @Override
    public List<Requests> getRequestsByUser(Long userId) {
        List<RequestsEntity> requests = requestsRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        List<ItemEntity> items = itemRepository.findAllByRequestsIn(requests);
        if (items.size() != 0) {
            Map<Long, Requests> requestsList = new HashMap<>();
            for (ItemEntity item : items) {
                if (requestsList.containsKey(item.getRequests().getId())) {
                    Requests request = requestsList.get(item.getRequests().getId());
                    List<ItemForRequestsResponse> itemsFromRequest = request.getItems();
                    ItemForRequestsResponse itemFromRequest = itemMapper.toItemForRequestsResponse(item);
                    itemFromRequest.setRequestId(request.getId());
                    itemsFromRequest.add(itemFromRequest);
                    request.setItems(itemsFromRequest);
                    requestsList.put(item.getRequests().getId(), request);
                } else {
                    RequestsEntity requestsEntity = item.getRequests();
                    Requests requests1 = mapper.toRequests(requestsEntity);
                    ItemForRequestsResponse itemFromRequest = itemMapper.toItemForRequestsResponse(item);
                    itemFromRequest.setRequestId(requests1.getId());
                    requests1.setItems(List.of(itemFromRequest));
                    requestsList.put(requests1.getId(), requests1);
                }
            }
            return new ArrayList<>(requestsList.values());
        } else {
            return mapper.toListRequests(requests);
        }
    }

    @Override
    public Requests getRequestsById(Long requestsId) {
        RequestsEntity requestsEntity = requestsRepository.findById(requestsId).get();
        Requests request = mapper.toRequests(requestsEntity);
        if (request.getItems() != null) {
            request.getItems().forEach(e -> e.setRequestId(request.getId()));
        }
        return request;
    }

    @Override
    public List<Requests> getRequestsByPage(Integer from, Integer size, Long userId) {
        Pageable pageParam = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.DESC, "created"));
        List<RequestsEntity> requestsEntities = requestsRepository.findAllByRequestorIdIsNot(userId, pageParam);
        if (requestsEntities.size() > 0) {
            int i = 3;
        }
        List<Requests> requests = mapper.toListRequests(requestsEntities);
        requests.stream().filter(f -> f.getItems() != null).forEach(i -> i.getItems().forEach(e -> e.setRequestId(i.getId())));
        return requests;
    }

    @Override
    public void checkRequests(Long requestsId) {
        if (!requestsRepository.existsById(requestsId)) {
            throw new DataNotFoundException("Запроса с id=" + requestsId + " не существует");
        }
    }
}
