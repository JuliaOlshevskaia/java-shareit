package ru.practicum.shareitserver.requests.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareitserver.requests.dto.Requests;
import ru.practicum.shareitserver.requests.entity.RequestsEntity;
import ru.practicum.shareitserver.requests.mapper.RequestsMapper;
import ru.practicum.shareitserver.user.dto.User;
import ru.practicum.shareitserver.user.entity.UserEntity;
import ru.practicum.shareitserver.user.mapper.UserMapper;
import ru.practicum.shareitserver.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RequestsServiceImplTest {
    private final EntityManager em;
    private final RequestsService service;
    private final RequestsMapper mapper;
    private final UserService userService;
    private final UserMapper userMapper;

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
    User userCreated;

    @BeforeAll
    void setUp() {
        userCreated = userService.create(user);
        requests.setRequestorId(userCreated.getId());
        requestsWithId.setRequestorId(userCreated.getId());
        requestsEntity.setRequestor(userMapper.toEntity(userCreated));
    }

    @Test
    void create() {
        Requests requestsCreated = service.create(requests);

        TypedQuery<RequestsEntity> query = em.createQuery("Select r from RequestsEntity r where r.id = :id", RequestsEntity.class);
        RequestsEntity entityCreated = query.setParameter("id", requestsCreated.getId()).getSingleResult();

        assertThat(entityCreated.getId(), notNullValue());
        assertThat(entityCreated.getDescription(), equalTo(requests.getDescription()));
        assertThat(entityCreated.getRequestor().getId(), equalTo(requests.getRequestorId()));
        assertThat(entityCreated.getCreated(), equalTo(requests.getCreated()));
    }

    @Test
    void getRequestsByUser() {
        List<Requests> requestsList = List.of(requestsWithId);

        RequestsEntity requestsEntityCreated = em.merge(requestsEntity);

        List<Requests> requestsGetting = service.getRequestsByUser(userCreated.getId());

        assertEquals(requestsGetting.size(), requestsList.size());
        assertThat(requestsGetting.get(0).getId(), equalTo(requestsEntityCreated.getId()));
        assertThat(requestsGetting.get(0).getDescription(), equalTo(requestsEntityCreated.getDescription()));
        assertThat(requestsGetting.get(0).getRequestorId(), equalTo(requestsEntityCreated.getRequestor().getId())); // можно добавить время без милисекунд
    }

    @Test
    void getRequestsById() {
        RequestsEntity requestsEntityCreated = em.merge(requestsEntity);

        Requests requestsGetting = service.getRequestsById(requestsEntityCreated.getId());

        assertThat(requestsGetting.getId(), equalTo(requestsEntityCreated.getId()));
        assertThat(requestsGetting.getDescription(), equalTo(requestsEntityCreated.getDescription()));
        assertThat(requestsGetting.getRequestorId(), equalTo(requestsEntityCreated.getRequestor().getId())); // можно добавить время без милисекунд
    }

    @Test
    void getRequestsByPage() {
        List<Requests> requestsList = List.of(requestsWithId);
        User user2 = new User(2L, "Name 2", "email2@mail.com");
        User userGetRequest = userService.create(user2);

        RequestsEntity requestsEntityCreated = em.merge(requestsEntity);

        List<Requests> requestsGetting = service.getRequestsByPage(0, 1, userGetRequest.getId());

        assertEquals(requestsGetting.size(), requestsList.size());
        assertThat(requestsGetting.get(0).getId(), equalTo(requestsEntityCreated.getId()));
        assertThat(requestsGetting.get(0).getDescription(), equalTo(requestsEntityCreated.getDescription()));
        assertThat(requestsGetting.get(0).getRequestorId(), equalTo(requestsEntityCreated.getRequestor().getId())); // можно добавить время без милисекунд
    }

    @Test
    void checkRequests() {
        RequestsEntity requestsEntityCreated = em.merge(requestsEntity);
        assertDoesNotThrow(() -> service.checkRequests(requestsEntityCreated.getId()));
    }
}
