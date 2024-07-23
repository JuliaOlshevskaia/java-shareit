package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @MockBean
    UserService userService;

    @MockBean
    ItemMapper itemMapper;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto = new ItemDto(
            "Name",
            "Description",
            true,
            null
            );

    private ItemResponse itemResponse = new ItemResponse(
            1L,
            "Name",
            "Description",
            true,
            null, null, null, null);

    private ItemUpdateDto itemUpdateDto = new ItemUpdateDto(
            "Name",
            "Description",
            true);

    private Item item = new Item(
            1L,
            "Name",
            "Description",
            true,
            1L,
            null, null, null, null);

    @Test
    void createNewItem() throws Exception {
        Mockito.doNothing().when(userService).checkUser(any());
        when(itemMapper.toItem(itemDto, 1L))
                .thenReturn(item);
        when(itemMapper.toResponse(item))
                .thenReturn(itemResponse);
        when(itemService.create(any()))
                .thenReturn(item);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponse.getId()))
                .andExpect(jsonPath("$.name").value(itemResponse.getName()))
                .andExpect(jsonPath("$.description").value(itemResponse.getDescription()))
                .andExpect(jsonPath("$.available").value(itemResponse.isAvailable()));
    }

    @Test
    void getSearchItems() throws Exception {
        String text = "test";
        Integer from = 0;
        Integer size = 10;
        Long userId = 1L;

        List<Item> items = new ArrayList<>();
        items.add(new Item(
                1L,
                "Name",
                "Description test",
                true,
                1L,
                null, null, null, null));
        items.add(new Item(
                2L,
                "Name2",
                "Description2 test",
                true,
                1L,
                null, null, null, null));
        when(itemService.getSearchItems(text, from, size)).thenReturn(items);

        List<ItemResponse> itemResponses = new ArrayList<>();
        itemResponses.add(new ItemResponse(
                1L,
                "Name",
                "Description",
                true,
                null, null, null, null));
        itemResponses.add(new ItemResponse(
                2L,
                "Name2",
                "Description2",
                true,
                null, null, null, null));

        when(itemMapper.toListResponse(items)).thenReturn(itemResponses);


        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .content(mapper.writeValueAsString(itemResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void update() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        Mockito.doNothing().when(userService).checkUser(any());
        when(itemService.update(itemId, item)).thenReturn(item);
        when(itemMapper.toItem(itemUpdateDto)).thenReturn(item);
        when(itemMapper.toResponse(item)).thenReturn(itemResponse);
        when(itemService.getItemById(itemId, userId)).thenReturn(item);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponse.getId()))
                .andExpect(jsonPath("$.name").value(itemResponse.getName()))
                .andExpect(jsonPath("$.description").value(itemResponse.getDescription()))
                .andExpect(jsonPath("$.available").value(itemResponse.isAvailable()));
    }

    @Test
    void getItems() throws Exception {
        Long userId = 1L;

        List<Item> items = new ArrayList<>();
        items.add(new Item(
                1L,
                "Name",
                "Description",
                true,
                1L,
                null, null, null, null));
        items.add(new Item(
                2L,
                "Name2",
                "Description2",
                true,
                1L,
                null, null, null, null));

        List<ItemResponse> itemResponses = new ArrayList<>();
        itemResponses.add(new ItemResponse(
                1L,
                "Name",
                "Description",
                true,
                null, null, null, null));
        itemResponses.add(new ItemResponse(
                2L,
                "Name2",
                "Description2",
                true,
                null, null, null, null));

        when(itemService.getItemsByUserId(userId))
                .thenReturn(items);
        when(itemMapper.toListResponse(items))
                .thenReturn(itemResponses);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getItemById() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;
        when(itemService.getItemById(itemId, userId)).thenReturn(item);
        when(itemMapper.toResponse(item)).thenReturn(itemResponse);


        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponse.getId()))
                .andExpect(jsonPath("$.name").value(itemResponse.getName()))
                .andExpect(jsonPath("$.description").value(itemResponse.getDescription()))
                .andExpect(jsonPath("$.available").value(itemResponse.isAvailable()));
    }

    @Test
    public void testCreateComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test comment");

        Comment comment1 = new Comment(1L, "Test comment", "Name", LocalDateTime.now());

        when(itemService.createComment(any(), any(), any())).thenReturn(comment1);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(comment1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment1.getId()))
                .andExpect(jsonPath("$.text").value(comment1.getText()))
                .andExpect(jsonPath("$.authorName").value(comment1.getAuthorName()));
    }
}
