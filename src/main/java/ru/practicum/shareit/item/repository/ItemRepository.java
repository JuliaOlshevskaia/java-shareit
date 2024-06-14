package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.user.entity.UserEntity;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    List<ItemEntity> findAllByOwnerOrderById(UserEntity owner);

    @Query("select ie from ItemEntity ie " +
            "where ie.available = true " +
            "and (lower(ie.name) like lower(concat('%', :text, '%')) " +
            "or lower(ie.description) like lower(concat('%', :text, '%')))")
    List<ItemEntity> search(@Param("text") String text);
}
