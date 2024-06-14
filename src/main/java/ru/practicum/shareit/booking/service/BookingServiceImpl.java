package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.dto.Booking;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private final BookingMapper mapper;

    @Override
    public BookingEntity create(Booking booking) {
        validate(booking);
        ItemEntity item = itemRepository.findById(booking.getItemId()).get();
        UserEntity user = userRepository.findById(booking.getBookerId()).get();
        if (item.getOwner().getId().equals(user.getId())) {
            throw new DataNotFoundException("Нельзя бронировать свою вещь");
        }
        if (item.getAvailable()) {
            BookingEntity bookingEntity = mapper.toEntity(booking);
            bookingEntity.setBooker(user);
            bookingEntity.setItem(item);
            return bookingRepository.save(bookingEntity);
        } else {
            throw new ValidationException("Вещь id=" + item.getId() + " не доступна для бронирования");
        }
    }

    @Override
    public BookingEntity changeStatus(Long bookingId,  boolean approved, Long userId) {
        checkBooking(bookingId);
        BookingEntity bookingEntity = bookingRepository.findById(bookingId).get();
        checkUserIsOwner(bookingEntity, userId);
        if (bookingEntity.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Бронирование уже подтверждено");
        }
        if (approved) {
            bookingEntity.setStatus(BookingStatus.APPROVED);
        } else {
            bookingEntity.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(bookingEntity);
        return bookingEntity;
    }

    @Override
    public BookingEntity getBookingById(Long bookingId, Long userId) {
        checkBooking(bookingId);
        BookingEntity bookingEntity = bookingRepository.findById(bookingId).get();
        if (!(bookingEntity.getItem().getOwner().getId().equals(userId) || bookingEntity.getBooker().getId().equals(userId))) {
            throw new DataNotFoundException("Пользователь не может запрашивать информацию");
        }
        return bookingEntity;
    }

    @Override
    public List<BookingEntity> getBookingsByUser(String text, Long userId) {
        List<BookingEntity> bookings;
        UserEntity user = userRepository.findById(userId).get();
        LocalDateTime timeNow = LocalDateTime.now();
        switch (text) {
            case ("ALL") :
                bookings = bookingRepository.findAllByBookerOrderByStartDesc(user);
                break;
            case ("CURRENT") :
                bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user, timeNow, timeNow);
                break;
            case ("PAST") :
                bookings = bookingRepository.findAllByBookerAndEndBeforeOrderByStartDesc(user, LocalDateTime.now());
                break;
            case ("FUTURE") :
                bookings = bookingRepository.findAllByBookerAndStartAfterOrderByStartDesc(user, LocalDateTime.now());
                break;
            case ("WAITING") :
                bookings = bookingRepository.findAllByBookerAndStatusOrderByStartDesc(user, BookingStatus.WAITING);
                break;
            case ("REJECTED") :
                bookings = bookingRepository.findAllByBookerAndStatusOrderByStartDesc(user, BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: " + text);
        }
        return bookings;
    }

    @Override
    public List<BookingEntity> getBookingsByOwner(String text, Long userId) {
        List<BookingEntity> bookings;
        UserEntity user = userRepository.findById(userId).get();
        LocalDateTime timeNow = LocalDateTime.now();
        switch (text) {
            case ("ALL") :
                bookings = bookingRepository.findAllByItemOwnerOrderByStartDesc(user);
                break;
            case ("CURRENT") :
                bookings = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(user, timeNow, timeNow);
                break;
            case ("PAST") :
                bookings = bookingRepository.findAllByItemOwnerAndEndBeforeOrderByStartDesc(user, LocalDateTime.now());
                break;
            case ("FUTURE") :
                bookings = bookingRepository.findAllByItemOwnerAndStartAfterOrderByStartDesc(user, LocalDateTime.now());
                break;
            case ("WAITING") :
                bookings = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(user, BookingStatus.WAITING);
                break;
            case ("REJECTED") :
                bookings = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(user, BookingStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: " + text);
        }
        return bookings;
    }

    private void checkUserIsOwner(BookingEntity booking, Long userId) {
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new DataNotFoundException("Пользователь id=" + userId + " не владелец вещи id=" + booking.getItem().getId());
        }
    }


    private void validate(Booking booking) {
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата окончания в прошлом");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Дата окончания раньше даты начала");
        }
        if (booking.getEnd().equals(booking.getStart())) {
            throw new ValidationException("Дата окончания и дата начала совпадает");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата старта в прошлом");
        }

    }

    private void checkBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new DataNotFoundException("Бронирования id=" + bookingId + " не существует");
        }
    }


}
