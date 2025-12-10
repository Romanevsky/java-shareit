package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@Rollback
public class ItemRequestServiceImplTest {


    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private UserRepository userRepository;

    //    private UserCreateDto userCreateDto;
    private User user;
    private ItemRequestCreateDto itemRequestCreateDto;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Test", "test@mail.com");
        user = userRepository.save(user);
        itemRequestCreateDto = new ItemRequestCreateDto("Test Request");
    }

    @Test
    void save() {
        ItemRequestDto createdRequest = itemRequestService.save(itemRequestCreateDto, user.getId());
        assertNotNull(createdRequest.getId());
        assertEquals(itemRequestCreateDto.getDescription(), createdRequest.getDescription());
    }

    @Test
    void getUserItemRequests() {
        itemRequestService.save(itemRequestCreateDto, user.getId());
        List<ItemRequestDto> requests = itemRequestService.getUserItemRequests(user.getId());
        assertFalse(requests.isEmpty());
    }

    @Test
    void getAllItemRequests() {
        itemRequestService.save(itemRequestCreateDto, user.getId());
        List<ItemRequestDto> requests = itemRequestService.getAllItemRequests(user.getId());
        assertTrue(requests.isEmpty());
    }

    @Test
    void getItemRequestById() {
        ItemRequestDto createdRequest = itemRequestService.save(itemRequestCreateDto, user.getId());
        ItemRequestDto foundRequest = itemRequestService.getItemRequestById(user.getId(), createdRequest.getId());
        assertEquals(createdRequest.getId(), foundRequest.getId());
    }

    @Test
    void getNotExistentItemRequest() {
        assertThrows(DataNotFoundException.class, () -> itemRequestService.getItemRequestById(user.getId(), 2L));
    }
}
