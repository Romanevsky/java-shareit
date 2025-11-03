package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    User createUser(User user);

    User updateUser(User user, Long userId);

    List<User> getAllUsers();

    User getUser(Long userId);

    void deleteUser(Long userId);
}