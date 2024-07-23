package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
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
import static org.mockito.Mockito.*;

public class ItemServiceTest {

    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private RequestsRepository requestsRepository;
    private ItemMapper mapper;
    private CommentMapper commentMapper;
    private ItemServiceImpl service;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        requestsRepository = mock(RequestsRepository.class);
        mapper = Mappers.getMapper(ItemMapper.class);
        commentMapper = Mappers.getMapper(CommentMapper.class);
        service = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository,
                requestsRepository, mapper, commentMapper);
    }

    @Test
    void create() {
        var item = new Item(null, "Name1", "description1", true, 1L,
                null, null, null, null);
        var user = new UserEntity();
        user.setId(1L);
        user.setName("Name1");
        user.setEmail("mail1@yandex.ru");
        var itemEntity = new ItemEntity();
        itemEntity.setName("Name1");
        itemEntity.setDescription("description1");
        itemEntity.setAvailable(true);
        itemEntity.setOwner(user);

        var itemWithId = new ItemEntity();
        itemWithId.setId(1L);
        itemWithId.setName("Name1");
        itemWithId.setDescription("description1");
        itemWithId.setAvailable(true);
        itemWithId.setOwner(user);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(itemWithId);

        var result = service.create(item);
        assertNotNull(result);
        assertEquals(itemWithId.getId(), result.getId());
    }

    @Test
    public void testUpdate() {
        Long itemId = 1L;
        Item item = new Item(itemId, "New Name", "New Description",true, null, null, null, null, null);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(itemId);
        itemEntity.setName("Old Name");
        itemEntity.setDescription("Old Description");
        itemEntity.setAvailable(false);

        ItemEntity itemEntityUpdated = new ItemEntity();
        itemEntityUpdated.setId(itemId);
        itemEntityUpdated.setName("New Name");
        itemEntityUpdated.setDescription("New Description");
        itemEntityUpdated.setAvailable(true);

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemEntity), Optional.of(itemEntityUpdated));

        Item updatedItem = service.update(itemId, item);

        assertEquals(item.getName(), updatedItem.getName());
        assertEquals(item.getDescription(), updatedItem.getDescription());
        assertEquals(item.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    public void getItemById_ValidItemId_ReturnsItem() {
        Long itemId = 1L;
        Long userId = 2L;

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(itemId);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setName("Name");
        userEntity.setEmail("mail@yandex.ru");
        itemEntity.setOwner(userEntity);

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemEntity));
        when(bookingRepository.findFirstByItemIdAndEndBeforeOrderByStartDesc(itemId, LocalDateTime.now())).thenReturn(null);
        when(bookingRepository.findFirstByItemIdAndStartAfterOrderByStart(itemId, LocalDateTime.now())).thenReturn(null);
        when(commentRepository.findAllByItemId(itemId)).thenReturn(null);

        Item item = service.getItemById(itemId, userId);

        assertNotNull(item);
        assertEquals(itemId, item.getId());
        assertEquals(userId, item.getUserId());
    }

    @Test
    public void testGetItemsByUserIdWithSize() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;

        UserEntity user = new UserEntity();
        user.setId(userId);

        Pageable pageParam = PageRequest.of(from > 0 ? from / size : 0, size);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(itemRepository.findAllByOwnerOrderById(user, pageParam)).thenReturn(new ArrayList<>());
        when(bookingRepository.findAllByItemInAndEndBeforeOrderByStartDesc(anyList(), any(LocalDateTime.class))).thenReturn(new ArrayList<>());
        when(bookingRepository.findFirstByItemInAndStartAfterOrderByStart(anyList(), any(LocalDateTime.class))).thenReturn(new ArrayList<>());

        List<Item> items = service.getItemsByUserId(userId, from, size);

        assertEquals(0, items.size());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findAllByOwnerOrderById(user, pageParam);
        verify(bookingRepository, times(1)).findAllByItemInAndEndBeforeOrderByStartDesc(anyList(), any(LocalDateTime.class));
        verify(bookingRepository, times(1)).findFirstByItemInAndStartAfterOrderByStart(anyList(), any(LocalDateTime.class));
    }

    @Test
    public void testGetItemsByUserId() {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        List<ItemEntity> itemEntities = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        List<BookingEntity> lastBooking = new ArrayList<>();
        List<BookingEntity> nextBooking = new ArrayList<>();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerOrderById(user)).thenReturn(itemEntities);
        when(bookingRepository.findAllByItemInAndEndBeforeOrderByStartDesc(itemEntities, LocalDateTime.now())).thenReturn(lastBooking);
        when(bookingRepository.findFirstByItemInAndStartAfterOrderByStart(itemEntities, LocalDateTime.now())).thenReturn(nextBooking);

        List<Item> result = service.getItemsByUserId(userId);

        assertEquals(items, result);
    }

    @Test
    public void test_getSearchItemsWithSize() {
        String text = "test";
        Integer from = 0;
        Integer size = 10;
        Pageable pageParam = PageRequest.of(from > 0 ? from / size : 0, size);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(1L);
        itemEntity.setName("Name");
        itemEntity.setDescription("Description test");
        itemEntity.setAvailable(true);

        List<ItemEntity> items = new ArrayList<>();
        items.add(itemEntity);
        when(itemRepository.search(text, pageParam)).thenReturn(items);

        List<Item> expected = new ArrayList<>();
        Item itemExpected = new Item(1L, "Name", "Description test", true, null, null, null, null, null);
        expected.add(itemExpected);

        List<Item> actual = service.getSearchItems(text, from, size);

        assertEquals(expected, actual);
    }

    @Test
    public void test_getSearchItems() {
        String text = "test";

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(1L);
        itemEntity.setName("Name");
        itemEntity.setDescription("Description test");
        itemEntity.setAvailable(true);

        List<ItemEntity> items = new ArrayList<>();
        items.add(itemEntity);
        when(itemRepository.search(text)).thenReturn(items);

        List<Item> expected = new ArrayList<>();
        Item itemExpected = new Item(1L, "Name", "Description test", true, null, null, null, null, null);
        expected.add(itemExpected);
        List<Item> actual = service.getSearchItems(text);

        assertEquals(expected, actual);
    }

    @Test
    public void testCheckItemFalse() {
        when(itemRepository.existsById(1L)).thenReturn(false);

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> service.checkItem(1L));

        verify(itemRepository, times(1)).existsById(1L);

        String expectedMessage = "Вещи с id=1 не существует";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void createComment() {
        String text = "text";
        LocalDateTime timeCreateComment = LocalDateTime.now();
         User user = new User(
                1L,
                "Name",
                "email@mail.com");
        UserEntity userEntity = new UserEntity(
                1L,
                "Name",
                "email@mail.com");

        Item item = new Item(
                null,
                "Name",
                "Description",
                true,
                1L,
                null, null, null, null);

        ItemEntity itemEntity = new ItemEntity(
                1L,
                "Name",
                "Description",
                true,
                userEntity, null);

        BookingEntity booking = new BookingEntity(1L, LocalDateTime.now(), LocalDateTime.now().minusSeconds(1),
                itemEntity, userEntity, BookingStatus.APPROVED);
        List<BookingEntity> bookingEntityList = List.of(booking);
        CommentEntity commentEntity = new CommentEntity(1L, text, itemEntity, userEntity, timeCreateComment);
        Comment comment = new Comment(1L, text, user.getName(), timeCreateComment);

        when(userRepository.findById(any())).thenReturn(Optional.of(userEntity));
        when(itemRepository.findById(any())).thenReturn(Optional.of(itemEntity));
        when(bookingRepository.findAllByBookerAndEndBeforeAndStatusAndItem(any(), any(), any(), any())).thenReturn(bookingEntityList);
        when(commentRepository.save(any())).thenReturn(commentEntity);

        var result = service.createComment(text, 1L, 1L);

        assertEquals(comment, result);
    }
}
