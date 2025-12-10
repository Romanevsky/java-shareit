package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserCreateDto userCreateDto) {
        log.info("Start creating user: {}", userCreateDto);
        UserDto user = userService.save(userCreateDto);
        log.info("Finish creating user: {}", user);
        return user;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserUpdateDto userUpdateDto, @PathVariable Long userId) {
        log.info("Start updating user: {}", userUpdateDto);
        UserDto user = userService.updateUser(userUpdateDto, userId);
        log.info("Finish updating user: {}", user);
        return user;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("Start getting user:{}", userId);
        UserDto user = userService.getUser(userId);
        log.info("Finish getting user:{}", userId);
        return user;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Start getting all users");
        List<UserDto> users = userService.getAllUsers();
        log.info("Finish getting all users");
        return users;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Start delete user:{}", userId);
        userService.deleteUser(userId);
        log.info("Finish delete user:{}", userId);
    }
}