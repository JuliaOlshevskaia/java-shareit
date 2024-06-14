package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;
    private final ItemMapper mapper;
    private final UserService userService;

    @PostMapping
    public ItemResponse create(@Valid @RequestBody ItemDto request, @RequestHeader("X-Sharer-User-Id") Long userId) {
        userService.checkUser(userId);
        Item item = mapper.toItem(request, userId);
        Item modified = service.create(item);
        return mapper.toResponse(modified);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse update(@Valid @RequestBody ItemUpdateDto request, @PathVariable Long itemId,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (!service.getItemById(itemId).getUserId().equals(userId)) {
            throw new DataNotFoundException("Пользователь, меняющий вещь, не ее владелец");
        }
        Item item = mapper.toItem(request);
        Item modified = service.update(itemId, item);
        return mapper.toResponse(modified);
    }

    @GetMapping("/{itemId}")
    public ItemResponse getItemById(@PathVariable Long itemId) {
        Item modified = service.getItemById(itemId);
        return mapper.toResponse(modified);
    }

    @GetMapping
    public List<ItemResponse> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<Item> items = service.getItemsByUserId(userId);
        return mapper.toListResponse(items);
    }

    @GetMapping("/search")
    public List<ItemResponse> getSearchItems(@RequestParam("text") String text, @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<Item> items = service.getSearchItems(text);
        return mapper.toListResponse(items);
    }
}
