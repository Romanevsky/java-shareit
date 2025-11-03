package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserBookerDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class BookingMapper {
    public Booking toBooking(BookingCreateDto bookingCreateDto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(bookingCreateDto.getStart());
        booking.setEnd(bookingCreateDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        return booking;
    }

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new ItemBookingDto(booking.getItem().getId(), booking.getItem().getName()),
                new UserBookerDto(booking.getBooker().getId()),
                booking.getStatus());
    }
}