package ru.practicum.shareitserver.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitserver.exceptions.DataNotFoundException;
import ru.practicum.shareitserver.item.dto.*;
import ru.practicum.shareitserver.item.mapper.ItemMapper;
import ru.practicum.shareitserver.item.service.ItemService;
import ru.practicum.shareitserver.user.service.UserService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;
    private final ItemMapper mapper;
    private final UserService userService;

    @PostMapping
    public ItemResponse create(@RequestBody ItemDto request, @RequestHeader("X-Sharer-User-Id") Long userId) {
        userService.checkUser(userId);
        Item item = mapper.toItem(request, userId);
        Item modified = service.create(item);
        return mapper.toResponse(modified);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse update(@RequestBody ItemUpdateDto request, @PathVariable Long itemId,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        userService.checkUser(userId);
        Item a = service.getItemById(itemId, userId);
        if (!service.getItemById(itemId, userId).getUserId().equals(userId)) {
            throw new DataNotFoundException("Пользователь, меняющий вещь, не ее владелец");
        }
        Item item = mapper.toItem(request);
        Item modified = service.update(itemId, item);
        return mapper.toResponse(modified);
    }

    @GetMapping("/{itemId}")
    public ItemResponse getItemById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item modified = service.getItemById(itemId, userId);
        return mapper.toResponse(modified);
    }

    @GetMapping
    public List<ItemResponse> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam(name = "from", required = false) Integer from,
                                       @RequestParam(name = "size", required = false) Integer size) {
        List<Item> items;
        if (from == null || size == null) {
            items = service.getItemsByUserId(userId);
        } else {
            items = service.getItemsByUserId(userId, from, size);
        }
        return mapper.toListResponse(items);
    }

    @GetMapping("/search")
    public List<ItemResponse> getSearchItems(@RequestParam("text") String text,
                                             @RequestParam(name = "from", required = false) Integer from,
                                             @RequestParam(name = "size", required = false) Integer size,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<Item> items;
        if (from == null || size == null) {
            items = service.getSearchItems(text);
        } else {
            items = service.getSearchItems(text, from, size);
        }
        return mapper.toListResponse(items);
    }

    @PostMapping("/{itemId}/comment")
    public Comment createComment(@RequestBody CommentDto comment, @PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.createComment(comment.getText(), itemId, userId);
    }
}
