package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findAllByBookerOrderByStartDesc(UserEntity user);

    List<BookingEntity> findAllByBookerAndEndBeforeOrderByStartDesc(UserEntity user, LocalDateTime nowTime);

    List<BookingEntity> findAllByBookerAndStartAfterOrderByStartDesc(UserEntity user, LocalDateTime nowTime);

    List<BookingEntity> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(UserEntity user, LocalDateTime nowTime, LocalDateTime nowTime2);

    List<BookingEntity> findAllByBookerAndStatusOrderByStartDesc(UserEntity user, BookingStatus status);

    List<BookingEntity> findAllByItemOwnerOrderByStartDesc(UserEntity user);

    List<BookingEntity> findAllByItemOwnerAndEndBeforeOrderByStartDesc(UserEntity user, LocalDateTime nowTime);

    List<BookingEntity> findAllByItemOwnerAndStartAfterOrderByStartDesc(UserEntity user, LocalDateTime nowTime);

    List<BookingEntity> findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(UserEntity user, LocalDateTime nowTime, LocalDateTime nowTime2);

    List<BookingEntity> findAllByItemOwnerAndStatusOrderByStartDesc(UserEntity user, BookingStatus status);

    BookingEntity findFirstByItemIdAndEndBeforeOrderByStartDesc(Long itemId, LocalDateTime nowTime);

    BookingEntity findFirstByItemIdAndStartAfterOrderByStart(Long itemId, LocalDateTime nowTime);

    BookingEntity findFirstByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime nowTime);

    List<BookingEntity> findAllByBookerAndEndBeforeAndStatusAndItem(UserEntity user, LocalDateTime nowTime, BookingStatus status, ItemEntity item);

    List<BookingEntity> findAllByItemInAndEndBeforeOrderByStartDesc(List<ItemEntity> items, LocalDateTime nowTime);

    List<BookingEntity> findFirstByItemInAndStartAfterOrderByStart(List<ItemEntity> items, LocalDateTime nowTime);
}
