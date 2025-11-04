package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    @Autowired
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                    @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        bookingCreateDto.setBookerId(bookerId);
        BookingDto booking = bookingService.save(bookingCreateDto);
        return booking;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto itemOwnerBookingDecision(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                               @RequestParam(value = "approved") Boolean approved,
                                               @PathVariable @Positive Long bookingId) {
        return bookingService.itemOwnerBookingDecision(ownerId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable @Positive Long bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findUserBookings(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                             @RequestParam(value = "state",
                                                     defaultValue = "ALL") String state) {
        return bookingService.findUserBookings(bookerId, State.valueOf(state));
    }

    @GetMapping("/owner")
    public List<BookingDto> findOwnerItemsBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                   @RequestParam(value = "state",
                                                           defaultValue = "ALL") String state) {
        return bookingService.findOwnerItemsBookings(ownerId, State.valueOf(state));
    }
}