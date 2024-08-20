package ru.practicum.shareitserver.item.service;

import ru.practicum.shareitserver.item.dto.Comment;
import ru.practicum.shareitserver.item.dto.Item;

import java.util.List;

public interface ItemService {
    Item create(Item item);

    Item update(Long itemId, Item item);

    Item getItemById(Long itemId, Long userId);

    List<Item> getItemsByUserId(Long userId, Integer from, Integer size);

    List<Item> getSearchItems(String text, Integer from, Integer size);

    List<Item> getItemsByUserId(Long userId);

    List<Item> getSearchItems(String text);

    void checkItem(Long itemId);

    Comment createComment(String text, Long itemId, Long userId);
}
