package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.Booking;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
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

@ExtendWith(MockitoExtension.class)
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

    Booking bookingOwnItem = new Booking(
            2L,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusDays(1),
            1L,
            1L,
            BookingStatus.WAITING);

    BookingEntity bookingEntityWithIdOwnItem = new BookingEntity(
            2L,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusDays(1),
            itemEntity,
            userEntity,
            BookingStatus.WAITING);

    private BookingEntity bookingEntityWithIdCurrent = new BookingEntity(
            3L,
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now().plusDays(1),
            itemEntity,
            bookerEntity,
            BookingStatus.APPROVED);

    private BookingEntity bookingEntityWithIdPast = new BookingEntity(
            4L,
            LocalDateTime.now().minusHours(2),
            LocalDateTime.now().minusHours(1),
            itemEntity,
            bookerEntity,
            BookingStatus.APPROVED);

    private BookingEntity bookingEntityWithIdFuture = new BookingEntity(
            5L,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusHours(2),
            itemEntity,
            bookerEntity,
            BookingStatus.APPROVED);

    private BookingEntity bookingEntityWithIdRejected = new BookingEntity(
            6L,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusDays(1),
            itemEntity,
            bookerEntity,
            BookingStatus.REJECTED);

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
    void createWithEndDateBeforeStartDate() {
        Booking bookingEndDateBeforeNow = new Booking(
                7L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().minusHours(1),
                1L,
                2L,
                BookingStatus.WAITING);

        assertThrows(ValidationException.class, () -> service.create(bookingEndDateBeforeNow));
    }

    @Test
    void createWithEndDateBeforeNow() {
        Booking bookingEndDateBeforeNow = new Booking(
                7L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                1L,
                2L,
                BookingStatus.WAITING);

        assertThrows(ValidationException.class, () -> service.create(bookingEndDateBeforeNow));
    }

    @Test
    void createWithEndDateEqualsStartDate() {
        Booking bookingEndDateBeforeNow = new Booking(
                7L,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusHours(1),
                1L,
                2L,
                BookingStatus.WAITING);

        assertThrows(ValidationException.class, () -> service.create(bookingEndDateBeforeNow));
    }

    @Test
    void createWithStartDateBeforeNow() {
        Booking bookingEndDateBeforeNow = new Booking(
                7L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusHours(1),
                1L,
                2L,
                BookingStatus.WAITING);

        assertThrows(ValidationException.class, () -> service.create(bookingEndDateBeforeNow));
    }

    @Test
    void createBookingOwnItemThrowException() {
        when(userRepository.findById(any())).thenReturn(Optional.of(userEntity));
        when(itemRepository.findById(any())).thenReturn(Optional.of(itemEntity));
//        when(bookingRepository.save(any())).thenReturn(bookingEntityWithIdOwnItem);

        assertThrows(DataNotFoundException.class, () -> service.create(bookingOwnItem));
    }

    @Test
    void createBookingNotAvailableItemThrowException() {
        ItemEntity itemEntityNotAvailable = new ItemEntity(
                2L,
                "Name2",
                "Description2",
                false,
                userEntity, null);

        Booking bookingNotAvailableItem = new Booking(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1),
                2L,
                2L,
                BookingStatus.WAITING);

        BookingEntity bookingEntityNotAvailableItem = new BookingEntity(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1),
                itemEntityNotAvailable,
                bookerEntity,
                BookingStatus.WAITING);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemEntityNotAvailable));

        assertThrows(ValidationException.class, () -> service.create(bookingNotAvailableItem));
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
    void changeStatusBookingApprovedThrowException() {
        BookingEntity bookingEntityApproved = new BookingEntity(
                3L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1),
                itemEntity,
                bookerEntity,
                BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingEntityApproved));
        when(bookingRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(ValidationException.class, () -> service.changeStatus(booking.getId(), true, user.getId()));
    }

    @Test
    void changeStatusNotOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingEntityWithId));
        when(bookingRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(DataNotFoundException.class, () -> service.changeStatus(booking.getId(), true, 100L));
    }

    @Test
    void changeStatusNotExistBooking() {
        assertThrows(DataNotFoundException.class, () -> service.changeStatus(100L, true, 100L));
    }

    @Test
    void changeStatusReject() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingEntityWithId));
        when(bookingRepository.save(any())).thenReturn(bookingEntityWithId);
        when(bookingRepository.existsById(anyLong())).thenReturn(true);

        var result = service.changeStatus(booking.getId(), false, user.getId());

        assertNotNull(result);
        assertEquals(bookingEntityWithId.getId(), result.getId());
        assertEquals(BookingStatus.REJECTED, result.getStatus());
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
    void getBookingByIdOwnItemThrowException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingEntityWithIdOwnItem));
        when(bookingRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(DataNotFoundException.class, () -> service.getBookingById(bookingOwnItem.getId(), 3L));
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
    void getCurrentBookingsByUserWithSize() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithIdCurrent);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByUser("CURRENT", booker.getId(), 0, 1);

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getPastBookingsByUserWithSize() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithIdPast);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByBookerAndEndBeforeOrderByStartDesc(any(), any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByUser("PAST", booker.getId(), 0, 1);

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getFutureBookingsByUserWithSize() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithIdFuture);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByBookerAndStartAfterOrderByStartDesc(any(), any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByUser("FUTURE", booker.getId(), 0, 1);

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getWaitingBookingsByUserWithSize() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByBookerAndStatusOrderByStartDesc(any(), any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByUser("WAITING", booker.getId(), 0, 1);

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getRejectedBookingsByUserWithSize() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithIdRejected);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByBookerAndStatusOrderByStartDesc(any(), any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByUser("REJECTED", booker.getId(), 0, 1);

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getBookingsByUserWithSizeWithWrongTypeThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));

        assertThrows(ValidationException.class, () -> service.getBookingsByUser("ALLL", user.getId(), 0, 1));
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
    void getCurrentBookingsByUser() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithIdCurrent);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByUser("CURRENT", booker.getId());

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getPastBookingsByUser() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithIdPast);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByBookerAndEndBeforeOrderByStartDesc(any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByUser("PAST", booker.getId());

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getFutureBookingsByUser() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithIdFuture);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByBookerAndStartAfterOrderByStartDesc(any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByUser("FUTURE", booker.getId());

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getWaitingBookingsByUser() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByBookerAndStatusOrderByStartDesc(any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByUser("WAITING", booker.getId());

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getRejectedBookingsByUser() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithIdRejected);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByBookerAndStatusOrderByStartDesc(any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByUser("REJECTED", booker.getId());

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getBookingsByUserWithWrongTypeThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));

        assertThrows(ValidationException.class, () -> service.getBookingsByUser("ALLL", user.getId()));
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
    void getCurrentBookingsByOwnerWithSize() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithIdCurrent);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByOwner("CURRENT", booker.getId(), 0, 1);

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getPastBookingsByOwnerWithSize() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithIdPast);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByItemOwnerAndEndBeforeOrderByStartDesc(any(), any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByOwner("PAST", booker.getId(), 0, 1);

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getFutureBookingsByOwnerWithSize() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithIdFuture);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByItemOwnerAndStartAfterOrderByStartDesc(any(), any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByOwner("FUTURE", booker.getId(), 0, 1);

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getWaitingBookingsByOwnerWithSize() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(any(), any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByOwner("WAITING", booker.getId(), 0, 1);

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getRejectedBookingsByOwnerWithSize() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithIdRejected);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(any(), any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByOwner("REJECTED", booker.getId(), 0, 1);

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getBookingsByOwnerWithSizeWithWrongTypeThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));

        assertThrows(ValidationException.class, () -> service.getBookingsByOwner("ALLL", user.getId(), 0, 1));
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

    @Test
    void getCurrentBookingsByOwner() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithIdCurrent);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByOwner("CURRENT", booker.getId());

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getPastBookingsByOwner() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithIdPast);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByItemOwnerAndEndBeforeOrderByStartDesc(any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByOwner("PAST", booker.getId());

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getFutureBookingsByOwner() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithIdFuture);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByItemOwnerAndStartAfterOrderByStartDesc(any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByOwner("FUTURE", booker.getId());

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getWaitingBookingsByOwner() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByOwner("WAITING", booker.getId());

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getRejectedBookingsByOwner() {
        List<BookingEntity> bookingsList = List.of(bookingEntityWithIdRejected);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerEntity));
        when(bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(any(), any())).thenReturn(bookingsList);

        var result = service.getBookingsByOwner("REJECTED", booker.getId());

        assertEquals(bookingsList.size(), result.size());
        assertEquals(bookingsList.get(0).getId(), result.get(0).getId());
    }

    @Test
    void getBookingsByOwnerWithWrongTypeThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));

        assertThrows(ValidationException.class, () -> service.getBookingsByOwner("ALLL", user.getId()));
    }
}
