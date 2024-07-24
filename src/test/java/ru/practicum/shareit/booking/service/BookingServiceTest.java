package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.Booking;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;

    private BookingMapper mapper;
    private BookingServiceImpl service;

    private User user = new User(
            1L,
            "John",
            "john.doe@mail.com");

    private UserEntity userEntity = new UserEntity(
            1L,
            "John",
            "john.doe@mail.com");

    private User booker = new User(
            2L,
            "Booker",
            "booker@mail.com");

    private UserEntity bookerEntity = new UserEntity(
            2L,
            "Booker",
            "booker@mail.com");

    private Item item = new Item(
            null,
            "Name",
            "Description",
            true,
            1L,
            null, null, null, null);

    private ItemEntity itemEntity = new ItemEntity(
            1L,
            "Name",
            "Description",
            true,
            userEntity, null);

    private BookingEntity bookingEntityWithId = new BookingEntity(
            1L,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusDays(1),
            itemEntity,
            bookerEntity,
            BookingStatus.WAITING);

    private Booking booking = new Booking(
            1L,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusDays(1),
            1L,
            2L,
            BookingStatus.WAITING);

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        mapper = Mappers.getMapper(BookingMapper.class);
        service = new BookingServiceImpl(bookingRepository, itemRepository, userRepository, mapper);
    }

    @Test
    void create() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemEntity));
        when(bookingRepository.save(any())).thenReturn(bookingEntityWithId);

        var result = service.create(booking);

        assertNotNull(result);
        assertEquals(bookingEntityWithId.getId(), result.getId());
    }

    @Test
    void changeStatus() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingEntityWithId));
        when(bookingRepository.save(any())).thenReturn(bookingEntityWithId);
        when(bookingRepository.existsById(anyLong())).thenReturn(true);

        var result = service.changeStatus(booking.getId(), true, user.getId());

        assertNotNull(result);
        assertEquals(bookingEntityWithId.getId(), result.getId());
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void getBookingById() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingEntityWithId));
        when(bookingRepository.existsById(anyLong())).thenReturn(true);

        var result = service.getBookingById(booking.getId(), user.getId());

        assertNotNull(result);
        assertEquals(bookingEntityWithId.getId(), result.getId());
        assertThat(bookingEntityWithId.getStart(), equalTo(result.getStart()));
        assertThat(bookingEntityWithId.getEnd(), equalTo(result.getEnd()));
        assertThat(bookingEntityWithId.getItem().getId(), equalTo(result.getItem().getId()));
        assertThat(bookingEntityWithId.getBooker().getId(), equalTo(result.getBooker().getId()));
        assertThat(bookingEntityWithId.getStatus(), equalTo(result.getStatus()));
    }

    @Test
    void getBookingsByUserWithSize() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByBookerOrderByStartDesc(any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByUser("ALL", booker.getId(), 0, 1);

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getBookingsByUser() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByBookerOrderByStartDesc(any())).thenReturn(bookingsList);

        var result = service.getBookingsByUser("ALL", booker.getId());

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getBookingsByOwnerWithSize() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));
        when(bookingRepository.findAllByItemOwnerOrderByStartDesc(any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByOwner("ALL", user.getId(), 0, 1);

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getBookingsByOwner() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));
        when(bookingRepository.findAllByItemOwnerOrderByStartDesc(any())).thenReturn(bookingsList);

        var result = service.getBookingsByOwner("ALL", user.getId());

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }
}
