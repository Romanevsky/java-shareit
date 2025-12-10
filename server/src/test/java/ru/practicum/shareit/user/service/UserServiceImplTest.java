package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
public class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        userCreateDto = new UserCreateDto("Test", "test@mail.com");
        userUpdateDto = new UserUpdateDto("Test update", "testupdate@mail.com");

    }

    @Test
    void save() {
        UserDto createdUser = userService.save(userCreateDto);
        assertNotNull(createdUser.getId());
        assertEquals(userCreateDto.getName(), createdUser.getName());
        assertEquals(userCreateDto.getEmail(), createdUser.getEmail());
    }

    @Test
    void updateUser() {
        UserDto createdUser = userService.save(userCreateDto);
        UserDto updatedUser = userService.updateUser(userUpdateDto, createdUser.getId());
        assertEquals("Test update", updatedUser.getName());
    }

    @Test
    void getAllUsers() {
        userService.save(userCreateDto);
        assertFalse(userService.getAllUsers().isEmpty());
    }

    @Test
    void getUser() {
        UserDto createdUser = userService.save(userCreateDto);
        UserDto foundUser = userService.getUser(createdUser.getId());
        assertEquals(createdUser.getId(), foundUser.getId());
    }

    @Test
    void deleteUser() {
        UserDto createdUser = userService.save(userCreateDto);
        userService.deleteUser(createdUser.getId());
        assertTrue(userService.getAllUsers().isEmpty());
    }

    @Test
    void updateNonExistentUser() {
        assertThrows(DataNotFoundException.class, () -> userService.updateUser(userUpdateDto, 2L));
    }

    @Test
    void getNotExistentUser() {
        assertThrows(DataNotFoundException.class, () -> userService.getUser(2L));
    }
}
