package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.dto.Item;

import java.util.List;

public interface ItemService {
    Item create(Item item);

    Item update(Long itemId, Item item);

    Item getItemById(Long itemId, Long userId);

    List<Item> getItemsByUserId(Long userId);

    List<Item> getSearchItems(String text);

    void checkItem(Long itemId);

    Comment createComment(String text, Long itemId, Long userId);
}
