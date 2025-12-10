package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.DataIsNotAvailableException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
public class ItemServiceImplTest {
    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private ItemCreateDto itemCreateDto;
    private ItemUpdateDto itemUpdateDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Test", "test@mail.com");
        user = userRepository.save(user);
        itemRequest = new ItemRequest(1L, "Item request description", user, LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);
        itemCreateDto = new ItemCreateDto(user.getId(), "Test Item", "Description", true, null);
        itemUpdateDto = new ItemUpdateDto(1L, user.getId(), "Updated Item", "Updated Description", false, null);

    }

    @Test
    void save() {
        ItemDto createdItem = itemService.save(itemCreateDto);
        assertNotNull(createdItem.getId());
        assertEquals(itemCreateDto.getName(), createdItem.getName());
    }

    @Test
    void saveWithRequest() {
        ItemCreateDto itemCreateDtoWithRequest = new ItemCreateDto(user.getId(), "Test Item", "Description", true, itemRequest.getId());
        ItemDto createdItem = itemService.save(itemCreateDtoWithRequest);
        assertNotNull(createdItem.getId());
        assertEquals(itemCreateDtoWithRequest.getName(), createdItem.getName());
    }

    @Test
    void updateItem() {
        ItemDto createdItem = itemService.save(itemCreateDto);
        itemUpdateDto.setId(createdItem.getId());
        ItemDto updatedItem = itemService.updateItem(itemUpdateDto);
        assertEquals(itemUpdateDto.getName(), updatedItem.getName());
        assertEquals(itemUpdateDto.getDescription(), updatedItem.getDescription());
        assertFalse(updatedItem.isAvailable());
    }

    @Test
    void getItem() {
        ItemDto createdItem = itemService.save(itemCreateDto);
        ItemInfoDto foundItem = itemService.getItem(createdItem.getId(), user.getId());
        assertEquals(createdItem.getId(), foundItem.getId());
    }

    @Test
    void getUserItems() {
        itemService.save(itemCreateDto);
        List<ItemInfoDto> items = itemService.getUserItems(user.getId());
        assertFalse(items.isEmpty());
    }

    @Test
    void getSearchItems() {
        itemService.save(itemCreateDto);
        List<ItemDto> items = itemService.getSearchItems("Test");
        assertFalse(items.isEmpty());
    }

    @Test
    void saveComment() {
        ItemDto createdItem = itemService.save(itemCreateDto);

        Booking booking = new Booking();
        booking.setItem(itemRepository.findById(createdItem.getId()).get());
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(Status.APPROVED);
        bookingRepository.save(booking);

        CommentCreateDto commentCreateDto = new CommentCreateDto("Test comment", createdItem.getId(), user.getId());
        CommentDto commentDto = itemService.saveComment(commentCreateDto);

        assertNotNull(commentDto.getId());
        assertEquals("Test comment", commentDto.getText());
    }

    @Test
    void saveCommentForNotExistentBooking() {
        ItemDto createdItem = itemService.save(itemCreateDto);

        Booking booking = new Booking();
        booking.setItem(itemRepository.findById(createdItem.getId()).get());
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(Status.APPROVED);
        bookingRepository.save(booking);
        CommentCreateDto commentCreateDto = new CommentCreateDto("Test comment", createdItem.getId(), 999L);

        assertThrows(DataIsNotAvailableException.class, () -> itemService.saveComment(commentCreateDto));
    }
}
