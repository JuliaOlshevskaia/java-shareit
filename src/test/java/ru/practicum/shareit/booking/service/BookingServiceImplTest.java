package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingServiceImplTest {
    private final EntityManager em;
    private final BookingService service;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper mapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    private User user = new User(
        null,
        "John",
        "john.doe@mail.com");

    private UserEntity userEntity = new UserEntity(
            1L,
            "John",
            "john.doe@mail.com");

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

    private BookingEntity bookingEntity = new BookingEntity(
            null,
            LocalDateTime.of(2021, 1, 1, 12, 0, 0),
            LocalDateTime.of(2021, 1, 1, 12, 30, 0),
            itemEntity,
            userEntity,
            BookingStatus.WAITING);

    private User booker = new User(
            null,
            "Booker",
            "booker@mail.com");
    User userCreated = new User();
    Item itemCreated = new Item();
    User bookerCreated = new User();

    @BeforeAll
     void setUp() {
        userCreated = userService.create(user);
        item.setUserId(userCreated.getId());
        itemCreated = itemService.create(item);
        bookerCreated = userService.create(booker);
        itemEntity = itemMapper.toEntity(itemCreated);
        itemEntity.setOwner(userMapper.toEntity(userCreated));
        bookingEntity.setItem(itemEntity);
        bookingEntity.setBooker(userMapper.toEntity(bookerCreated));
    }

    @Test
    void create() {
         Booking booking = new Booking(
                null,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1),
                1L,
                 bookerCreated.getId(),
                BookingStatus.WAITING);

        BookingEntity bookingCreated = service.create(booking);

        TypedQuery<BookingEntity> query = em.createQuery("Select b from BookingEntity b where b.id = :id", BookingEntity.class);
        BookingEntity entityCreated = query.setParameter("id", bookingCreated.getId()).getSingleResult();

        assertThat(entityCreated.getId(), notNullValue());
        assertThat(entityCreated.getStart(), equalTo(booking.getStart()));
        assertThat(entityCreated.getEnd(), equalTo(booking.getEnd()));
        assertThat(entityCreated.getItem().getId(), equalTo(booking.getItemId()));
        assertThat(entityCreated.getBooker().getId(), equalTo(booking.getBookerId()));
        assertThat(entityCreated.getStatus(), equalTo(booking.getStatus()));
    }

    @Test
    void changeStatus() {
        em.merge(bookingEntity);
        em.flush();

        List<BookingEntity> bookings = service.getBookingsByOwner("ALL", userCreated.getId());
        BookingEntity bookingCreated = bookings.get(0);

        BookingEntity bookingChanged = service.changeStatus(bookings.get(0).getId(), true, userCreated.getId());

        bookingEntity.setStatus(BookingStatus.APPROVED);

        assertThat(bookingCreated.getId(), equalTo(bookingChanged.getId()));
        assertThat(bookingCreated.getStart(), equalTo(bookingChanged.getStart()));
        assertThat(bookingCreated.getEnd(), equalTo(bookingChanged.getEnd()));
        assertThat(bookingCreated.getItem().getId(), equalTo(bookingChanged.getItem().getId()));
        assertThat(bookingCreated.getBooker().getId(), equalTo(bookingChanged.getBooker().getId()));
        assertThat(bookingCreated.getStatus(), equalTo(bookingChanged.getStatus()));
    }

    @Test
    void getBookingById() {
        em.merge(bookingEntity);
        em.flush();

        List<BookingEntity> bookings = service.getBookingsByOwner("ALL", userCreated.getId());
        BookingEntity bookingCreated = bookings.get(0);

        BookingEntity bookingGetting = service.getBookingById(bookings.get(0).getId(), userCreated.getId());

        assertThat(bookingCreated.getId(), equalTo(bookingGetting.getId()));
        assertThat(bookingCreated.getStart(), equalTo(bookingGetting.getStart()));
        assertThat(bookingCreated.getEnd(), equalTo(bookingGetting.getEnd()));
        assertThat(bookingCreated.getItem().getId(), equalTo(bookingGetting.getItem().getId()));
        assertThat(bookingCreated.getBooker().getId(), equalTo(bookingGetting.getBooker().getId()));
        assertThat(bookingCreated.getStatus(), equalTo(bookingGetting.getStatus()));
    }

    @Test
    void getBookingsByUserWithSize() {
        em.merge(bookingEntity);
        em.flush();

        List<BookingEntity> bookings = service.getBookingsByUser("ALL", bookerCreated.getId(), 0, 1);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), notNullValue());
        assertThat(bookings.get(0).getStart(), equalTo(bookingEntity.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(bookingEntity.getEnd()));
        assertThat(bookings.get(0).getItem().getId(), equalTo(bookingEntity.getItem().getId()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(bookingEntity.getBooker().getId()));
        assertThat(bookings.get(0).getStatus(), equalTo(bookingEntity.getStatus()));
    }

    @Test
    void getBookingsByUser() {
        em.merge(bookingEntity);
        em.flush();

        List<BookingEntity> bookings = service.getBookingsByUser("ALL", bookerCreated.getId());

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), notNullValue());
        assertThat(bookings.get(0).getStart(), equalTo(bookingEntity.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(bookingEntity.getEnd()));
        assertThat(bookings.get(0).getItem().getId(), equalTo(bookingEntity.getItem().getId()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(bookingEntity.getBooker().getId()));
        assertThat(bookings.get(0).getStatus(), equalTo(bookingEntity.getStatus()));
    }

    @Test
    void getBookingsByOwnerWithSize() {
        em.persist(bookingEntity);
        em.flush();

        List<BookingEntity> bookings = service.getBookingsByOwner("ALL", userCreated.getId(), 0, 1);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), notNullValue());
        assertThat(bookings.get(0).getStart(), equalTo(bookingEntity.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(bookingEntity.getEnd()));
        assertThat(bookings.get(0).getItem().getId(), equalTo(bookingEntity.getItem().getId()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(bookingEntity.getBooker().getId()));
        assertThat(bookings.get(0).getStatus(), equalTo(bookingEntity.getStatus()));
    }

    @Test
    void getBookingsByOwner() {
        em.merge(bookingEntity);
        em.flush();

        List<BookingEntity> bookings = service.getBookingsByOwner("ALL", userCreated.getId());

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), notNullValue());
        assertThat(bookings.get(0).getStart(), equalTo(bookingEntity.getStart()));
        assertThat(bookings.get(0).getEnd(), equalTo(bookingEntity.getEnd()));
        assertThat(bookings.get(0).getItem().getId(), equalTo(bookingEntity.getItem().getId()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(bookingEntity.getBooker().getId()));
        assertThat(bookings.get(0).getStatus(), equalTo(bookingEntity.getStatus()));
    }
}
