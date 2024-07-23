package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.entity.ItemEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository repository;

    @Test
    void search() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setName("Item name");
        itemEntity.setDescription("Description");
        itemEntity.setAvailable(true);

        ItemEntity itemEntityCreated = repository.save(itemEntity);

        List<ItemEntity> itemsSearched = repository.search("item");

        assertEquals(itemsSearched.size(), 1);
        assertEquals(itemsSearched.get(0).getId(), itemEntityCreated.getId());
        assertEquals(itemsSearched.get(0).getName(), itemEntityCreated.getName());
        assertEquals(itemsSearched.get(0).getDescription(), itemEntityCreated.getDescription());
        assertEquals(itemsSearched.get(0).getAvailable(), itemEntityCreated.getAvailable());
    }

    @Test
    void searchWithSize() {
        Integer from = 0;
        Integer size = 1;

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setName("Item name");
        itemEntity.setDescription("Description");
        itemEntity.setAvailable(true);

        ItemEntity itemEntityCreated = repository.save(itemEntity);

        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setName("Item name1");
        itemEntity1.setDescription("Description1");
        itemEntity1.setAvailable(true);

        ItemEntity itemEntity1Created = repository.save(itemEntity1);

        Pageable pageParam = PageRequest.of(from > 0 ? from / size : 0, size);

        List<ItemEntity> itemsSearched = repository.search("1", pageParam);

        assertEquals(itemsSearched.size(), 1);
        assertEquals(itemsSearched.get(0).getId(), itemEntity1Created.getId());
        assertEquals(itemsSearched.get(0).getName(), itemEntity1Created.getName());
        assertEquals(itemsSearched.get(0).getDescription(), itemEntity1Created.getDescription());
        assertEquals(itemsSearched.get(0).getAvailable(), itemEntity1Created.getAvailable());
    }
}
