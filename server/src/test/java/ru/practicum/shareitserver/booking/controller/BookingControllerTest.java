package ru.practicum.shareitserver.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareitserver.item.service.ItemService;
import ru.practicum.shareitserver.user.dto.User;
import ru.practicum.shareitserver.booking.dto.Booking;
import ru.practicum.shareitserver.booking.dto.BookingDto;
import ru.practicum.shareitserver.booking.dto.BookingResponse;
import ru.practicum.shareitserver.booking.entity.BookingEntity;
import ru.practicum.shareitserver.booking.enums.BookingStatus;
import ru.practicum.shareitserver.booking.mapper.BookingMapper;
import ru.practicum.shareitserver.booking.service.BookingService;
import ru.practicum.shareitserver.item.dto.Item;
import ru.practicum.shareitserver.item.entity.ItemEntity;
import ru.practicum.shareitserver.user.entity.UserEntity;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @MockBean
    BookingService bookingService;

    @MockBean
    BookingMapper bookingMapper;

    @Autowired
    private MockMvc mvc;

    private Item item = new Item(
            1L,
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
            null, null);

    private User user = new User(
            1L,
            "John",
            "john.doe@mail.com");

    private UserEntity userEntity = new UserEntity(
            1L,
            "John",
            "john.doe@mail.com");

    private BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.of(2021, 1, 1, 12, 0, 0),
            LocalDateTime.of(2021, 1, 1, 12, 30, 0)
            );

    private BookingResponse bookingResponse = new BookingResponse(
            1L,
            LocalDateTime.of(2021, 1, 1, 12, 0, 0),
            LocalDateTime.of(2021, 1, 1, 12, 30, 0),
            item,
            user,
            BookingStatus.APPROVED
    );

    private BookingEntity bookingEntity = new BookingEntity(
            1L,
            LocalDateTime.of(2021, 1, 1, 12, 0, 0),
            LocalDateTime.of(2021, 1, 1, 12, 30, 0),
            itemEntity,
            userEntity,
            BookingStatus.APPROVED);

    private Booking booking = new Booking(
            1L,
            LocalDateTime.of(2021, 1, 1, 12, 0, 0),
            LocalDateTime.of(2021, 1, 1, 12, 30, 0),
            1L,
            1L,
            BookingStatus.APPROVED);

    private Long userId = 1L;


    @Test
    void createBooking() throws Exception {
        booking.setStatus(BookingStatus.WAITING);
        Mockito.doNothing().when(itemService).checkItem(any());
        when(bookingMapper.toBooking(bookingDto, userId, BookingStatus.WAITING))
                .thenReturn(booking);
        when(bookingMapper.toBookingResponse(bookingEntity))
                .thenReturn(bookingResponse);
        when(bookingService.create(any()))
                .thenReturn(bookingEntity);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$.start").value(DateTimeFormatter.ISO_DATE_TIME.format(bookingResponse.getStart())))
                .andExpect(jsonPath("$.end").value(DateTimeFormatter.ISO_DATE_TIME.format(bookingResponse.getEnd())))
                .andExpect(jsonPath("$.status").value(bookingResponse.getStatus().toString()));
    }

    @Test
    void approved() throws Exception {
        when(bookingMapper.toBookingResponse(bookingEntity))
                .thenReturn(bookingResponse);
        when(bookingService.changeStatus(1L, true, userId))
                .thenReturn(bookingEntity);

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$.start").value(DateTimeFormatter.ISO_DATE_TIME.format(bookingResponse.getStart())))
                .andExpect(jsonPath("$.end").value(DateTimeFormatter.ISO_DATE_TIME.format(bookingResponse.getEnd())))
                .andExpect(jsonPath("$.status").value(bookingResponse.getStatus().toString()));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingMapper.toBookingResponse(bookingEntity))
                .thenReturn(bookingResponse);
        when(bookingService.getBookingById(1L, userId))
                .thenReturn(bookingEntity);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$.start").value(DateTimeFormatter.ISO_DATE_TIME.format(bookingResponse.getStart())))
                .andExpect(jsonPath("$.end").value(DateTimeFormatter.ISO_DATE_TIME.format(bookingResponse.getEnd())))
                .andExpect(jsonPath("$.status").value(bookingResponse.getStatus().toString()));
    }

    @Test
    void getBookingsByUser() throws Exception {
        List<BookingResponse> bookingResponseList = new ArrayList<>();
        bookingResponseList.add(bookingResponse);
        List<BookingEntity> bookingEntityList = new ArrayList<>();
        bookingEntityList.add(bookingEntity);

        when(bookingMapper.toListBookingResponse(bookingEntityList))
                .thenReturn(bookingResponseList);
        when(bookingService.getBookingsByUser("ALL", userId))
                .thenReturn(bookingEntityList);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingResponseList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookingResponseList.size()));
    }

    @Test
    void getBookingsByUserWithSize() throws Exception {
        String text = "ALL";
        Integer from = 0;
        Integer size = 1;

        List<BookingResponse> bookingResponseList = new ArrayList<>();
        bookingResponseList.add(bookingResponse);
        List<BookingEntity> bookingEntityList = new ArrayList<>();
        bookingEntityList.add(bookingEntity);

        when(bookingMapper.toListBookingResponse(bookingEntityList))
                .thenReturn(bookingResponseList);
        when(bookingService.getBookingsByUser(text, userId, from, size))
                .thenReturn(bookingEntityList);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .content(mapper.writeValueAsString(bookingResponseList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookingResponseList.size()));
    }

    @Test
    void getBookingsByOwner() throws Exception {
        List<BookingResponse> bookingResponseList = new ArrayList<>();
        bookingResponseList.add(bookingResponse);
        List<BookingEntity> bookingEntityList = new ArrayList<>();
        bookingEntityList.add(bookingEntity);

        when(bookingMapper.toListBookingResponse(bookingEntityList))
                .thenReturn(bookingResponseList);
        when(bookingService.getBookingsByOwner("ALL", userId))
                .thenReturn(bookingEntityList);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingResponseList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookingResponseList.size()));
    }

    @Test
    void getBookingsByOwnerWithSize() throws Exception {
        String text = "ALL";
        Integer from = 0;
        Integer size = 1;

        List<BookingResponse> bookingResponseList = new ArrayList<>();
        bookingResponseList.add(bookingResponse);
        List<BookingEntity> bookingEntityList = new ArrayList<>();
        bookingEntityList.add(bookingEntity);

        when(bookingMapper.toListBookingResponse(bookingEntityList))
                .thenReturn(bookingResponseList);
        when(bookingService.getBookingsByOwner(text, userId, from, size))
                .thenReturn(bookingEntityList);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .content(mapper.writeValueAsString(bookingResponseList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookingResponseList.size()));
    }
}
