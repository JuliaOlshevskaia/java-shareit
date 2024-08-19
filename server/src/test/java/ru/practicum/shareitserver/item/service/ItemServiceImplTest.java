package ru.practicum.shareitserver.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareitserver.item.dto.Comment;
import ru.practicum.shareitserver.item.entity.CommentEntity;
import ru.practicum.shareitserver.item.entity.ItemEntity;
import ru.practicum.shareitserver.booking.entity.BookingEntity;
import ru.practicum.shareitserver.booking.enums.BookingStatus;
import ru.practicum.shareitserver.booking.service.BookingService;
import ru.practicum.shareitserver.item.dto.Item;
import ru.practicum.shareitserver.item.mapper.ItemMapper;
import ru.practicum.shareitserver.item.repository.ItemRepository;
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
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@AutoConfigureTestDatabase
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemServiceImplTest {
    private final EntityManager em;
    private final ItemService service;
    private final UserService userService;
    private final ItemMapper mapper;
    private final UserMapper userMapper;
    private final ItemRepository repository;
    private final BookingService bookingService;

    User user = new User(null, "Name", "email@yandex.ru");
    User userCreated;

    @BeforeAll
    void setUp() {
        userCreated = userService.create(user);
    }

    @Test
    void create() {
        Item item = new Item(null, "Name", "Description", true, userCreated.getId(), null, null, null, null);
        Item itemCreated = service.create(item);

        TypedQuery<ItemEntity> query = em.createQuery("Select e from ItemEntity e where e.id = :id", ItemEntity.class);
        ItemEntity entityCreated = query.setParameter("id", itemCreated.getId()).getSingleResult();

        assertThat(entityCreated.getId(), notNullValue());
        assertThat(entityCreated.getName(), equalTo(item.getName()));
        assertThat(entityCreated.getDescription(), equalTo(item.getDescription()));
        assertThat(entityCreated.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    void getItemById() {
        Item item = new Item(null, "Name1", "Description1", true, userCreated.getId(), null, null, null, null);
        ItemEntity itemCreated = mapper.toEntity(item);
        itemCreated.setOwner(userMapper.toEntity(userCreated));

        em.persist(itemCreated);
        Long id = itemCreated.getId();
        em.flush();

        Item targetItem = service.getItemById(id, userCreated.getId());

        assertThat(targetItem, allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(item.getName())),
                hasProperty("description", equalTo(item.getDescription())),
                hasProperty("available", equalTo(item.getAvailable()))
        ));
    }

    @Test
    public void getItemsByUserIdWithSize() {
        Long userId = userCreated.getId();
        Integer from = 0;
        Integer size = 10;

        UserEntity userEntity = userMapper.toEntity(userCreated);

        List<Item> sourceItems = List.of(
                new Item(null, "Name1", "Description1", true, userCreated.getId(), null, null, null, null),
                new Item(null, "Name2", "Description2", true, userCreated.getId(), null, null, null, null),
                new Item(null, "Name3", "Description3", true, userCreated.getId(), null, null, null, null)
        );

        for (Item item : sourceItems) {
            ItemEntity entity = mapper.toEntity(item);
            entity.setOwner(userEntity);
            em.persist(entity);
        }
        em.flush();

        List<Item> targetItems = service.getItemsByUserId(userId, from, size);

        assertThat(targetItems, hasSize(sourceItems.size()));
        for (Item itemSource : sourceItems) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(itemSource.getName())),
                    hasProperty("description", equalTo(itemSource.getDescription())),
                    hasProperty("available", equalTo(itemSource.getAvailable()))
            )));
        }
    }

    @Test
    public void getItemsByUserId() {
        Long userId = userCreated.getId();

        UserEntity userEntity = userMapper.toEntity(userCreated);

        List<Item> sourceItems = List.of(
                new Item(null, "Name1", "Description1", true, userCreated.getId(), null, null, null, null),
                new Item(null, "Name2", "Description2", true, userCreated.getId(), null, null, null, null),
                new Item(null, "Name3", "Description3", true, userCreated.getId(), null, null, null, null)
        );

        for (Item item : sourceItems) {
            ItemEntity entity = mapper.toEntity(item);
            entity.setOwner(userEntity);
            em.persist(entity);
        }
        em.flush();

        List<Item> targetItems = service.getItemsByUserId(userId);

        assertThat(targetItems, hasSize(sourceItems.size()));
        for (Item itemSource : sourceItems) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(itemSource.getName())),
                    hasProperty("description", equalTo(itemSource.getDescription())),
                    hasProperty("available", equalTo(itemSource.getAvailable()))
            )));
        }
    }

    @Test
    public void getSearchItemsWithSize() {
        String text = "test";
        Integer from = 0;
        Integer size = 10;

        Item item = new Item(null, "Name1", "Description test", true, userCreated.getId(), null, null, null, null);
        ItemEntity itemCreated = mapper.toEntity(item);
        itemCreated.setOwner(userMapper.toEntity(userCreated));

        List<Item> sourceItems = List.of(item);
        em.persist(itemCreated);
        em.flush();

        List<Item> targetItems = service.getSearchItems(text, from, size);

        assertThat(targetItems, hasSize(sourceItems.size()));
        for (Item itemSource : sourceItems) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(itemSource.getName())),
                    hasProperty("description", equalTo(itemSource.getDescription())),
                    hasProperty("available", equalTo(itemSource.getAvailable()))
            )));
        }
    }

    @Test
    public void getSearchItems() {
        String text = "test";

        Item item = new Item(null, "Name1", "Description test", true, userCreated.getId(), null, null, null, null);
        ItemEntity itemCreated = mapper.toEntity(item);
        itemCreated.setOwner(userMapper.toEntity(userCreated));

        List<Item> sourceItems = List.of(item);
        em.persist(itemCreated);
        em.flush();

        List<Item> targetItems = service.getSearchItems(text);

        assertThat(targetItems, hasSize(sourceItems.size()));
        for (Item itemSource : sourceItems) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(itemSource.getName())),
                    hasProperty("description", equalTo(itemSource.getDescription())),
                    hasProperty("available", equalTo(itemSource.getAvailable()))
            )));
        }
    }

    @Test
    void checkItem() {
        Item item = new Item(null, "Name1", "Description test", true, userCreated.getId(), null, null, null, null);
        ItemEntity itemEntity = mapper.toEntity(item);
        User user = new User(1L, "Name4", "email4@yandex.ru");
        User user1 = userService.create(user);
        UserEntity userEntity1 = userMapper.toEntity(user1);
        itemEntity.setOwner(userEntity1);

        em.persist(itemEntity);
        Long id = itemEntity.getId();
        em.flush();

        assertDoesNotThrow(() -> service.checkItem(id));
    }

    @Test
    void createComment() {
        String text = "Comment text";

        User booker = new User(null, "Name1", "email1@yandex.ru");
        User bookerCreated = userService.create(booker);

        Item item = new Item(null, "Name", "Description", true, userCreated.getId(), null, null, null, null);
        Item itemCreated = service.create(item);

        BookingEntity booking = new BookingEntity(null, LocalDateTime.now(), LocalDateTime.now().minusSeconds(1),
                mapper.toEntity(itemCreated), userMapper.toEntity(bookerCreated), BookingStatus.APPROVED);
        em.persist(booking);

        Comment comment = new Comment(null, text, bookerCreated.getName(), LocalDateTime.now());

        service.createComment(text, itemCreated.getId(), bookerCreated.getId());

        TypedQuery<CommentEntity> query = em.createQuery("Select e from CommentEntity e where e.text = :text", CommentEntity.class);
        CommentEntity commentEntity = query.setParameter("text", comment.getText()).getSingleResult();

        assertThat(commentEntity.getId(), notNullValue());
        assertThat(commentEntity.getText(), equalTo(comment.getText()));
        assertThat(commentEntity.getItem().getId(), equalTo(itemCreated.getId()));
        assertThat(commentEntity.getAuthor().getName(), equalTo(comment.getAuthorName()));
    }

    @Test
    void update() {

        UserEntity userEntity = userMapper.toEntity(userCreated);

        List<Item> sourceItems = List.of(
                new Item(null, "Name1", "Description1", true, userCreated.getId(), null, null, null, null),
                new Item(null, "Name2", "Description2", true, userCreated.getId(), null, null, null, null),
                new Item(null, "Name3", "Description3", true, userCreated.getId(), null, null, null, null)
        );
        Long idItem2 = null;

        for (Item item : sourceItems) {
            ItemEntity entity = mapper.toEntity(item);
            entity.setOwner(userEntity);
            em.persist(entity);
            if (entity.getName().equals("Name2")) {
                idItem2 = entity.getId();
            }
        }
        em.flush();

        Item newItemId2 = new Item(idItem2, "NewName2", "NewDescription2", true, userCreated.getId(), null, null, null, null);

        service.update(idItem2, newItemId2);
        Item targetItems = service.getItemById(idItem2, userCreated.getId());

        assertThat(targetItems, allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(newItemId2.getName())),
                hasProperty("description", equalTo(newItemId2.getDescription())),
                hasProperty("available", equalTo(newItemId2.getAvailable()))
        ));
    }
}
