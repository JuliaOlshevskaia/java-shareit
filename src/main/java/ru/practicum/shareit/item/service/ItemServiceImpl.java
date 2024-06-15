package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortInfo;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper mapper;
    private final CommentMapper commentMapper;

    @Override
    public Item create(Item item) {
        ItemEntity itemEntity = mapper.toEntity(item);
        UserEntity user = userRepository.findById(item.getUserId()).get();
        itemEntity.setOwner(user);
        ItemEntity itemCreated = itemRepository.save(itemEntity);
        return mapper.toItem(itemCreated);
    }

    @Override
    public Item update(Long itemId, Item item) {
        if (itemRepository.existsById(itemId)) {
            ItemEntity itemOld = itemRepository.findById(itemId).get();
            ItemEntity newItem = new ItemEntity(itemId, item.getName() == null ? itemOld.getName() : item.getName(),
                    item.getDescription() == null ? itemOld.getDescription() : item.getDescription(),
                    item.getAvailable() == null ? itemOld.getAvailable() : item.getAvailable(),
                    itemOld.getOwner()
            );
            itemRepository.save(newItem);
        }
        return mapper.toItem(itemRepository.findById(itemId).get());
    }

    @Override
    public Item getItemById(Long itemId, Long userId) {
        if (!itemRepository.existsById(itemId)) {
            throw new DataNotFoundException("Вещи с id=" + itemId + " не существует");
        }
        ItemEntity itemEntity = itemRepository.findById(itemId).get();
        Item item = mapper.toItem(itemEntity);

        if (itemEntity.getOwner().getId().equals(userId)) {
            BookingEntity lastBooking = bookingRepository.findFirstByItemIdAndEndBeforeOrderByStartDesc(itemId, LocalDateTime.now());
            BookingEntity nextBooking = bookingRepository.findFirstByItemIdAndStartAfterOrderByStart(itemId, LocalDateTime.now());
            if (lastBooking != null) {
                item.setLastBooking(new BookingShortInfo(lastBooking.getId(), lastBooking.getBooker().getId()));
            } else {
                BookingEntity nowBooking = bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(itemId, LocalDateTime.now());
                if (nowBooking != null) {
                    item.setLastBooking(new BookingShortInfo(nowBooking.getId(), nowBooking.getBooker().getId()));
                }
            }
            if (nextBooking != null && !(nextBooking.getStatus().equals(BookingStatus.REJECTED))) {
                item.setNextBooking(new BookingShortInfo(nextBooking.getId(), nextBooking.getBooker().getId()));
            }
        }
        List<CommentEntity> commentsEntity = commentRepository.findAllByItemId(itemId);
        item.setComments(commentMapper.toListComment(commentsEntity));
        return item;
    }

    @Override
    public List<Item> getItemsByUserId(Long userId) {
        UserEntity user = userRepository.findById(userId).get();
        List<ItemEntity> itemEntities = itemRepository.findAllByOwnerOrderById(user);
        Map<Long, Item> itemMap = new HashMap<>();
        itemEntities.forEach(i -> itemMap.put(i.getId(), mapper.toItem(i)));
        List<BookingEntity> lastBooking = bookingRepository.findAllByItemInAndEndBeforeOrderByStartDesc(itemEntities, LocalDateTime.now());
        List<BookingEntity> nextBooking = bookingRepository.findFirstByItemInAndStartAfterOrderByStart(itemEntities, LocalDateTime.now());

        lastBooking.forEach(i -> {
            if (itemMap.get(i.getItem().getId()).getLastBooking() == null) {
                Item item = itemMap.get(i.getItem().getId());
                item.setLastBooking(new BookingShortInfo(i.getId(), i.getBooker().getId()));
                itemMap.put(item.getId(), item);
            }
        });

        nextBooking.forEach(i -> {
            if (itemMap.get(i.getItem().getId()).getNextBooking() == null) {
                Item item = itemMap.get(i.getItem().getId());
                item.setNextBooking(new BookingShortInfo(i.getId(), i.getBooker().getId()));
                itemMap.put(item.getId(), item);
            }
        });
        return new ArrayList<>(itemMap.values());
    }

    @Override
    public List<Item> getSearchItems(String text) {
        List<Item> itemsSearched = new ArrayList<>();
        if (!text.isBlank()) {
            itemsSearched = mapper.toListItem(itemRepository.search(text));
        }
        return itemsSearched;
    }

    @Override
    public void checkItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new DataNotFoundException("Вещи с id=" + itemId + " не существует");
        }
    }

    @Override
    public Comment createComment(String text, Long itemId, Long userId) {
        ItemEntity item = itemRepository.findById(itemId).get();
        UserEntity user = userRepository.findById(userId).get();
        List<BookingEntity> bookings = bookingRepository.findAllByBookerAndEndBeforeAndStatusAndItem(user,
                LocalDateTime.now(), BookingStatus.APPROVED, item);
        if (bookings.isEmpty()) {
            throw new ValidationException("Пользователь ранее не бронировал вещь");
        }

        CommentEntity commentEntity = new CommentEntity(null, text, item, user, LocalDateTime.now());
        CommentEntity commentCreated = commentRepository.save(commentEntity);

        return commentMapper.toComment(commentCreated);
    }
}
