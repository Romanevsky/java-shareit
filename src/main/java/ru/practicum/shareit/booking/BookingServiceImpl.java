// ... импорты без изменений ...
package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.DataIsNotAvailableException;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto save(BookingCreateDto bookingCreateDto) {
        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new DataNotFoundException("Такой вещи нет в базе"));
        User booker = userRepository.findById(bookingCreateDto.getBookerId())
                .orElseThrow(() -> new DataNotFoundException("Такого пользователя нет в базе"));
        if (!item.getAvailable()) {
            throw new DataIsNotAvailableException("Данная вещь недоступна");
        }
        Booking booking = bookingMapper.toBooking(bookingCreateDto, item, booker);
        booking.setStatus(Status.WAITING);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto itemOwnerBookingDecision(Long ownerId, Boolean approved, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException("Такого бронировния нет в базе"));
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new DataIsNotAvailableException("Изменить статус бронирования может только владелец вещи");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException("Такого бронирования нет в базе"));
        if (booking.getItem().getOwner().getId() == userId || booking.getBooker().getId() == userId) {
            return bookingMapper.toBookingDto(booking);
        }
        return null;
    }

    @Override
    public List<BookingDto> findUserBookings(Long bookerId, State state) {
        List<Booking> bookingList = bookingRepository.findByBookerId(bookerId)
                .stream()
                .sorted(Comparator
                        .comparing(Booking::getStart)
                        .reversed())
                .toList();
        return stateFilter(bookingList, state).stream().map(bookingMapper::toBookingDto).toList();
    }

    @Override
    public List<BookingDto> findOwnerItemsBookings(Long ownerId, State state) {
        if (itemRepository.findByOwnerId(ownerId).isEmpty()) {
            throw new DataNotFoundException("У этого пользователя нет вещей");
        }
        List<Booking> bookingList = bookingRepository.findByItemOwnerId(ownerId)
                .stream()
                .sorted(Comparator
                        .comparing(Booking::getStart)
                        .reversed())
                .toList();
        return stateFilter(bookingList, state).stream().map(bookingMapper::toBookingDto).toList();
    }

    public List<Booking> stateFilter(List<Booking> bookingList, State state) {
        switch (state) {
            case State.CURRENT -> bookingList = bookingList
                    .stream()
                    .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                    .filter(b -> b.getEnd().isAfter(LocalDateTime.now()))
                    .toList();
            case State.PAST -> bookingList = bookingList
                    .stream()
                    .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                    .toList();
            case State.FUTURE -> bookingList = bookingList
                    .stream()
                    .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                    .toList();
            case State.WAITING -> bookingList = bookingList
                    .stream()
                    .filter(b -> b.getStatus().equals(Status.WAITING))
                    .toList();
            case State.REJECTED -> bookingList = bookingList
                    .stream()
                    .filter(b -> b.getStatus().equals(Status.REJECTED))
                    .toList();
        }
        return bookingList;
    }
}
