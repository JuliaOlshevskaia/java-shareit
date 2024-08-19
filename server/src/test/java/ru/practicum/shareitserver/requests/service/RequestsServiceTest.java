package ru.practicum.shareitserver.requests.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareitserver.exceptions.DataNotFoundException;
import ru.practicum.shareitserver.item.entity.ItemEntity;
import ru.practicum.shareitserver.item.mapper.ItemMapper;
import ru.practicum.shareitserver.item.repository.ItemRepository;
import ru.practicum.shareitserver.requests.dto.Requests;
import ru.practicum.shareitserver.requests.entity.RequestsEntity;
import ru.practicum.shareitserver.requests.mapper.RequestsMapper;
import ru.practicum.shareitserver.requests.repository.RequestsRepository;
import ru.practicum.shareitserver.user.dto.User;
import ru.practicum.shareitserver.user.entity.UserEntity;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private ItemEntity itemEntity = new ItemEntity(
            1L,
            "Name",
            "Description",
            true,
            userEntity, requestsEntity);



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
    void getRequestsByUserWithItem() {
        requestsEntity.setItems(Set.of(itemEntity));

        List<RequestsEntity> requestsEntityList = List.of(requestsEntity);
        List<ItemEntity> itemEntityList = List.of(itemEntity);

        when(itemRepository.findAllByRequestsIn(any())).thenReturn(itemEntityList);
        when(requestsRepository.findAllByRequestorIdOrderByCreatedDesc(any())).thenReturn(requestsEntityList);

        var result = service.getRequestsByUser(user.getId());

        assertNotNull(result);
        assertEquals(requestsEntity.getId(), result.get(0).getId());
        assertEquals(requestsEntity.getDescription(), result.get(0).getDescription());
        assertEquals(requestsEntity.getRequestor().getId(), result.get(0).getRequestorId());
        assertEquals(requestsEntity.getCreated(), result.get(0).getCreated());
        assertEquals(itemEntity.getId(), result.get(0).getItems().get(0).getId());
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
    void getRequestsByIdWithItem() {
        requestsEntity.setItems(Set.of(itemEntity));

        when(requestsRepository.findById(any())).thenReturn(Optional.ofNullable(requestsEntity));

        var result = service.getRequestsById(requests.getId());

        assertNotNull(result);
        assertEquals(requestsEntity.getId(), result.getId());
        assertEquals(requestsEntity.getDescription(), result.getDescription());
        assertEquals(requestsEntity.getRequestor().getId(), result.getRequestorId());
        assertEquals(requestsEntity.getCreated(), result.getCreated());
        assertEquals(itemEntity.getId(), result.getItems().get(0).getId());
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
        assertThrows(DataNotFoundException.class, () -> service.checkRequests(requests.getId()));
    }
}
