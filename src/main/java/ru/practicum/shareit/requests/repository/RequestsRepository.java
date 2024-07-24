package ru.practicum.shareit.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.requests.entity.RequestsEntity;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface RequestsRepository extends JpaRepository<RequestsEntity, Long> {
    List<RequestsEntity> findAllByRequestorIdOrderByCreatedDesc(Long userId);

    List<RequestsEntity> findAllByRequestorIdIsNot(Long userId, Pageable pageable);
}
