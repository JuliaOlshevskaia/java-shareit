package ru.practicum.shareitserver.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareitserver.item.entity.ItemEntity;
import ru.practicum.shareitserver.user.entity.UserEntity;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository repository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void search() {
        UserEntity user = new UserEntity();
        user.setName("Name1");
        user.setEmail("mail1@yandex.ru");
        UserEntity userWithId = userRepository.save(user);
        ItemEntity itemEntity = new ItemEntity(1L, "Item name",
                "Description", true, userWithId, null);
        itemEntity.setOwner(userWithId);

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

        UserEntity user = new UserEntity();
        user.setName("Name2");
        user.setEmail("mail2@yandex.ru");
        UserEntity userWithId = userRepository.save(user);

        ItemEntity itemEntity = new ItemEntity(2L, "Item name",
                "Description", true, userWithId, null);

        ItemEntity itemEntityCreated = repository.save(itemEntity);

        ItemEntity itemEntity1 = new ItemEntity(3L, "Item name1",
                "Description1", true, userWithId, null);

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
