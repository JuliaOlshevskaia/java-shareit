package ru.practicum.shareit.requests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.requests.dto.Requests;
import ru.practicum.shareit.requests.dto.RequestsDescription;
import ru.practicum.shareit.requests.dto.RequestsResponse;
import ru.practicum.shareit.requests.mapper.RequestsMapper;
import ru.practicum.shareit.requests.service.RequestsService;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestsController.class)
public class RequestsControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    RequestsService requestsService;

    @MockBean
    UserService userService;

    @MockBean
    RequestsMapper requestsMapper;

    @Autowired
    private MockMvc mvc;

    String description = "Request";
    Requests requests = new Requests(1L, "Requestttt", 1L, LocalDateTime.now(), null);
    RequestsDescription requestsDescription = new RequestsDescription(description);
    RequestsResponse requestsResponse = new RequestsResponse(1L, "Request", LocalDateTime.now(), null);

    List<Requests> requestsList = List.of(requests);
    List<RequestsResponse> responseList = List.of(requestsResponse);

    @Test
    void create() throws Exception {
        Mockito.doNothing().when(userService).checkUser(any());
        doReturn(requests).when(requestsService).create(any());
        doReturn(requests).when(requestsMapper).toRequests(any(), any(), any());
        doReturn(requestsResponse).when(requestsMapper).toResponse(any());

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(requestsResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestsResponse.getId()))
                .andExpect(jsonPath("$.description").value(requestsResponse.getDescription()))
                .andExpect(jsonPath("$.created").value(DateTimeFormatter.ISO_DATE_TIME.format(requestsResponse.getCreated())));
    }


    @Test
    void getRequestByUserId() throws Exception {
        Mockito.doNothing().when(userService).checkUser(any());
        when(requestsService.getRequestsByUser(1L)).thenReturn(requestsList);
        when(requestsMapper.toListResponse(requestsList)).thenReturn(responseList);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(responseList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(responseList.size()));
    }

    @Test
    void getRequestById() throws Exception {
        Mockito.doNothing().when(userService).checkUser(any());
        Mockito.doNothing().when(requestsService).checkRequests(any());
        when(requestsService.getRequestsById(anyLong())).thenReturn(requests);
        when(requestsMapper.toResponse(requests)).thenReturn(requestsResponse);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(requestsResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestsResponse.getId()))
                .andExpect(jsonPath("$.description").value(requestsResponse.getDescription()))
                .andExpect(jsonPath("$.created").value(DateTimeFormatter.ISO_DATE_TIME.format(requestsResponse.getCreated())));
    }

    @Test
    void getRequestByPage() throws Exception {
        Mockito.doNothing().when(userService).checkUser(any());
        when(requestsService.getRequestsByPage(anyInt(), anyInt(), anyLong())).thenReturn(requestsList);
        when(requestsMapper.toListResponse(requestsList)).thenReturn(responseList);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(1))
                        .content(mapper.writeValueAsString(responseList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(responseList.size()));
    }

    @Test
    void getRequestByPageWrongSizeThrowException() throws Exception {
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(-1))
                        .content(mapper.writeValueAsString(responseList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
