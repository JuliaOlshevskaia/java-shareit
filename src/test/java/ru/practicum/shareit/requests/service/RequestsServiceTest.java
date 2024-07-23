package ru.practicum.shareit.requests.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.Requests;
import ru.practicum.shareit.requests.entity.RequestsEntity;
import ru.practicum.shareit.requests.mapper.RequestsMapper;
import ru.practicum.shareit.requests.repository.RequestsRepository;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestsServiceTest {
    private RequestsRepository requestsRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private RequestsMapper mapper;
    private ItemMapper itemMapper;
    private RequestsServiceImpl service;

    private Requests requests = new Requests(null, "Request", 1L, LocalDateTime.now(), null);
    private Requests requestsWithId = new Requests(1L, "Request", 1L, LocalDateTime.now(), null);
    private UserEntity userEntity = new UserEntity(
            1L,
            "Name",
            "email@mail.com");
    private User user = new User(
            1L,
            "Name",
            "email@mail.com");
    private RequestsEntity requestsEntity = new RequestsEntity(1L, "Request", userEntity, LocalDateTime.now(), null);

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        requestsRepository = mock(RequestsRepository.class);
        mapper = Mappers.getMapper(RequestsMapper.class);
        itemMapper = Mappers.getMapper(ItemMapper.class);
        service = new RequestsServiceImpl(requestsRepository, userRepository, itemRepository, mapper, itemMapper);
    }

    @Test
    void create() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));
        when(requestsRepository.save(any())).thenReturn(requestsEntity);

        var result = service.create(requests);

        assertNotNull(result);
        assertEquals(requestsEntity.getId(), result.getId());
    }

    @Test
    void getRequestsByUser() {
        List<RequestsEntity> requestsEntityList = List.of(requestsEntity);
        List<ItemEntity> itemEntityList = new ArrayList<>();

        when(itemRepository.findAllByRequestsIn(any())).thenReturn(itemEntityList);
        when(requestsRepository.findAllByRequestorIdOrderByCreatedDesc(any())).thenReturn(requestsEntityList);

        var result = service.getRequestsByUser(user.getId());

        assertNotNull(result);
        assertEquals(requestsEntity.getId(), result.get(0).getId());
        assertEquals(requestsEntity.getDescription(), result.get(0).getDescription());
        assertEquals(requestsEntity.getRequestor().getId(), result.get(0).getRequestorId());
        assertEquals(requestsEntity.getCreated(), result.get(0).getCreated());
    }

    @Test
    void getRequestsById() {
        when(requestsRepository.findById(any())).thenReturn(Optional.ofNullable(requestsEntity));

        var result = service.getRequestsById(requests.getId());

        assertNotNull(result);
        assertEquals(requestsEntity.getId(), result.getId());
        assertEquals(requestsEntity.getDescription(), result.getDescription());
        assertEquals(requestsEntity.getRequestor().getId(), result.getRequestorId());
        assertEquals(requestsEntity.getCreated(), result.getCreated());
    }

    @Test
    void getRequestsByPage() {
        List<RequestsEntity> requestsEntityList = List.of(requestsEntity);

        when(requestsRepository.findAllByRequestorIdIsNot(any(), any())).thenReturn(requestsEntityList);

        var result = service.getRequestsByPage(0, 1, user.getId());

        assertNotNull(result);
        assertEquals(requestsEntity.getId(), result.get(0).getId());
        assertEquals(requestsEntity.getDescription(), result.get(0).getDescription());
        assertEquals(requestsEntity.getRequestor().getId(), result.get(0).getRequestorId());
        assertEquals(requestsEntity.getCreated(), result.get(0).getCreated());
    }

    @Test
    void checkRequests() {
        when(requestsRepository.existsById(any())).thenReturn(false);
        assertThrows(DataNotFoundException.class, () -> {service.checkRequests(requests.getId());});
    }
}
