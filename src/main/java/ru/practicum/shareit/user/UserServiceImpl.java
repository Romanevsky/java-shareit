package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserCreateDto userCreateDto) {
        try {
            User user = userRepository.createUser(userMapper.toUser(userCreateDto));
            return userMapper.toUserDto(user);
        } catch (AlreadyExistException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserDto updateUser(UserUpdateDto userUpdateDto, Long userId) {
        try {
            User updatedUser = userRepository.updateUser(userMapper.toUser(userUpdateDto), userId);
            return userMapper.toUserDto(updatedUser);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userRepository.getUser(userId);
        if (user == null) {
            throw new RuntimeException("Пользователь с id " + userId + " не найден");
        }
        return userMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }
}

