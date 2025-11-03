package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto save(BookingCreateDto bookingCreateDto);

    BookingDto itemOwnerBookingDecision(Long ownerId, Boolean approved, Long bookingId);

    BookingDto findById(Long userId, Long bookingId);

    List<BookingDto> findUserBookings(Long bookerId, State state);

    List<BookingDto> findOwnerItemsBookings(Long ownerId, State state);

}