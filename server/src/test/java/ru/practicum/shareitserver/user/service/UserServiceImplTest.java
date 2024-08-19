package ru.practicum.shareitserver.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareitserver.user.dto.User;
import ru.practicum.shareitserver.user.entity.UserEntity;
import ru.practicum.shareitserver.user.mapper.UserMapper;
import ru.practicum.shareitserver.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class UserServiceImplTest {
    private final EntityManager em;
    private final UserService service;
    private final UserMapper mapper;
    private final UserRepository repository;

    @Test
    void create() {
        User user = new User(null, "Name", "emaillll@yandex.ru");
        service.create(user);

        TypedQuery<UserEntity> query = em.createQuery("Select u from UserEntity u where u.email = :email", UserEntity.class);
        UserEntity userCreated = query.setParameter("email", user.getEmail()).getSingleResult();

        assertThat(userCreated.getId(), notNullValue());
        assertThat(userCreated.getName(), equalTo(user.getName()));
        assertThat(userCreated.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void getAllUsers() {
        List<User> sourceUsers = List.of(
                new User(null, "Name1", "email1@yandex.ru"),
                new User(null, "Name2", "email2@yandex.ru"),
                new User(null, "Name3", "email3@yandex.ru")
        );

        for (User user : sourceUsers) {
            UserEntity entity = mapper.toEntity(user);
            em.persist(entity);
        }
        em.flush();

        List<User> targetUsers = service.getAllUsers();

        for (User sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void getUserById() {
        User user = new User(null, "Name1", "email1@yandex.ru");
        UserEntity entity = mapper.toEntity(user);

        em.persist(entity);
        em.flush();

        User targetUser = service.getUserById(entity.getId());

        assertThat(targetUser, allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(user.getName())),
                    hasProperty("email", equalTo(user.getEmail()))
            ));
    }

    @Test
    void delete() {
        List<User> sourceUsers = List.of(
                new User(null, "Name1", "email1@yandex.ru"),
                new User(null, "Name2", "email2@yandex.ru"),
                new User(null, "Name3", "email3@yandex.ru")
        );
        Long idUser2 = null;

        for (User user : sourceUsers) {
            UserEntity entity = mapper.toEntity(user);
            em.persist(entity);
            if (entity.getName().equals("Name2")) {
                idUser2 = entity.getId();
            }
        }
        em.flush();

        List<User> sourceUsersWithoutId2 = List.of(
                new User(null, "Name1", "email1@yandex.ru"),
                new User(null, "Name3", "email3@yandex.ru")
        );

        service.delete(idUser2);
        List<User> targetUsers = service.getAllUsers();

        for (User sourceUser : sourceUsersWithoutId2) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void update() {
        List<User> sourceUsers = List.of(
                new User(null, "Name1", "email1@yandex.ru"),
                new User(null, "Name2", "email2@yandex.ru"),
                new User(null, "Name3", "email3@yandex.ru")
        );
        Long idUser2 = null;

        for (User user : sourceUsers) {
            UserEntity entity = mapper.toEntity(user);
            em.persist(entity);
            if (entity.getName().equals("Name2")) {
                idUser2 = entity.getId();
            }
        }
        em.flush();

        User newUserId2 = new User(idUser2, "NewName2", "email2@yandex.ru");

        List<User> newSourceUsers = List.of(
                new User(null, "Name1", "email1@yandex.ru"),
                new User(null, "NewName2", "email2@yandex.ru"),
                new User(null, "Name3", "email3@yandex.ru")
        );

        service.update(idUser2, newUserId2);
        List<User> targetUsers = service.getAllUsers();

        for (User sourceUser : newSourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void checkUser() {
        User user = new User(null, "Name1", "email1@yandex.ru");
        UserEntity entity = mapper.toEntity(user);

        em.persist(entity);
        Long id = entity.getId();
        em.flush();
        assertDoesNotThrow(() -> service.checkUser(id));
    }
}
