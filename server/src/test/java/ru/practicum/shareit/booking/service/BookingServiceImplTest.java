package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.DataIsNotAvailableException;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
public class BookingServiceImplTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner User", "owner@mail.com");
        owner = userRepository.save(owner);

        booker = new User(2L, "Booker User", "booker@mail.com");
        booker = userRepository.save(booker);

        item = new Item(1L, "Test Item", "Description", true, owner, null);
        item = itemRepository.save(item);

        bookingCreateDto = new BookingCreateDto(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), booker.getId());
    }

    @Test
    void save() {
        BookingDto bookingDto = bookingService.save(bookingCreateDto);
        assertNotNull(bookingDto.getId());
        assertEquals(Status.WAITING, bookingDto.getStatus());
    }

    @Test
    void itemOwnerBookingDecision() {
        BookingDto bookingDto = bookingService.save(bookingCreateDto);
        BookingDto approvedBooking = bookingService.itemOwnerBookingDecision(owner.getId(), true, bookingDto.getId());
        assertEquals(Status.APPROVED, approvedBooking.getStatus());
    }

    @Test
    void findById() {
        BookingDto bookingDto = bookingService.save(bookingCreateDto);
        BookingDto foundBooking = bookingService.findById(booker.getId(), bookingDto.getId());
        assertEquals(bookingDto.getId(), foundBooking.getId());
    }

    @Test
    void findUserBookings() {
        bookingService.save(bookingCreateDto);
        List<BookingDto> bookings = bookingService.findUserBookings(booker.getId(), State.ALL);
        assertFalse(bookings.isEmpty());
    }

    @Test
    void findOwnerItemsBookings() {
        bookingService.save(bookingCreateDto);
        List<BookingDto> bookings = bookingService.findOwnerItemsBookings(owner.getId(), State.ALL);
        assertFalse(bookings.isEmpty());
    }

    @Test
    void saveBookingForNotAvailableItem() {
        item.setAvailable(false);
        itemRepository.save(item);
        assertThrows(DataIsNotAvailableException.class, () -> bookingService.save(bookingCreateDto));
    }

    @Test
    void wrongItemOwnerBookingDecision() {
        BookingDto bookingDto = bookingService.save(bookingCreateDto);
        assertThrows(DataIsNotAvailableException.class, () -> bookingService.itemOwnerBookingDecision(booker.getId(), true, bookingDto.getId()));
    }

    @Test
    void findByIdWithWrongUser() {
        BookingDto bookingDto = bookingService.save(bookingCreateDto);
        assertNull(bookingService.findById(999L, bookingDto.getId()));
    }

    @Test
    void findOwnerItemsBookingsEmptyList() {
        bookingService.save(bookingCreateDto);
        assertThrows(DataNotFoundException.class, () -> bookingService.findOwnerItemsBookings(999L, State.ALL));
    }

    @Test
    void findOwnerItemsFutureBookings() {
        bookingService.save(bookingCreateDto);
        List<BookingDto> bookings = bookingService.findOwnerItemsBookings(owner.getId(), State.FUTURE);
        assertFalse(bookings.isEmpty());
    }

    @Test
    void itemOwnerBookingDecisionReject() {
        BookingDto bookingDto = bookingService.save(bookingCreateDto);
        BookingDto approvedBooking = bookingService.itemOwnerBookingDecision(owner.getId(), false, bookingDto.getId());
        assertEquals(Status.REJECTED, approvedBooking.getStatus());
    }
}
