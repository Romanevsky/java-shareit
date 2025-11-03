package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.IsNotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;


    @Override
    public BookingDto save(BookingCreateDto bookingCreateDto) {
        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Такой вещи нет в базе"));
        User booker = userRepository.findById(bookingCreateDto.getBookerId())
                .orElseThrow(() -> new NotFoundException("Такого пользователя нет в базе"));
        if (!item.getAvailable()) {
            throw new IsNotAvailableException("Данная вещь недоступна");
        }
        Booking booking = bookingMapper.toBooking(bookingCreateDto, item, booker);
        booking.setStatus(Status.WAITING);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto itemOwnerBookingDecision(Long ownerId, Boolean approved, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Такого бронирования нет в базе"));
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new IsNotAvailableException("Изменить статус бронирования может только владелец вещи");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto findById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Такого бронирования нет в базе"));
        if (booking.getItem().getOwner().getId() == userId || booking.getBooker().getId() == userId) {
            return bookingMapper.toBookingDto(booking);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findUserBookings(Long bookerId, State state) {
        List<Booking> bookingList = bookingRepository.findByBookerId(bookerId)
                .stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .toList();
        return stateFilter(bookingList, state).stream().map(bookingMapper::toBookingDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findOwnerItemsBookings(Long ownerId, State state) {
        if (itemRepository.findByOwnerId(ownerId).isEmpty()) {
            throw new NotFoundException("У этого пользователя нет вещей");
        }
        List<Booking> bookingList = bookingRepository.findByItemOwnerId(ownerId)
                .stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .toList();
        return stateFilter(bookingList, state).stream().map(bookingMapper::toBookingDto).toList();
    }

    @Transactional(readOnly = true)
    public List<Booking> stateFilter(List<Booking> bookingList, State state) {
        return switch (state) {
            case CURRENT -> bookingList.stream()
                    .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                    .filter(b -> b.getEnd().isAfter(LocalDateTime.now()))
                    .toList();
            case PAST -> bookingList.stream()
                    .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                    .toList();
            case FUTURE -> bookingList.stream()
                    .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                    .toList();
            case WAITING -> bookingList.stream()
                    .filter(b -> b.getStatus() == Status.WAITING)
                    .toList();
            case REJECTED -> bookingList.stream()
                    .filter(b -> b.getStatus() == Status.REJECTED)
                    .toList();
            default -> throw new IllegalStateException("Unexpected value: " + state);
        };
    }
}
