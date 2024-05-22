package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private HashMap<Long, Item> items = new HashMap<>();
    Long generatedId = 0L;

    @Override
    public Item create(Item item) {
        ++generatedId;
        item.setId(generatedId);
        items.put(generatedId, item);
        return items.get(generatedId);
    }

    @Override
    public Item update(Long itemId, Item item) {
        if (items.containsKey(itemId)) {
            Item itemOld = items.get(itemId);
            Item newItem = new Item(itemId, item.getName() == null ? itemOld.getName() : item.getName(),
                    item.getDescription() == null ? itemOld.getDescription() : item.getDescription(),
                    item.getAvailable() == null ? itemOld.getAvailable() : item.getAvailable(),
                    itemOld.getUserId()
                    );
            items.put(itemId, newItem);
        }
        return items.get(itemId);
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByUserId(Long userId) {
        List<Item> itemsByUserId = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getUserId().equals(userId)) {
                itemsByUserId.add(item);
            }
        }
        return itemsByUserId;
    }

    @Override
    public List<Item> getSearchItems(String text) {
        List<Item> itemsSearched = new ArrayList<>();
        if (!text.isBlank()) {
            for (Item item : items.values()) {
                if ((item.getName().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                && item.getAvailable()) {
                    itemsSearched.add(item);
                }
            }
        }
        return itemsSearched;
    }
}
