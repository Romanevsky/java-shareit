package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserCreateDto userCreateDto) {
        return userMapper.toUserDto(userRepository.createUser(userMapper.toUser(userCreateDto)));
    }

    @Override
    public UserDto updateUser(UserUpdateDto userUpdateDto, Long userId) {
        return userMapper.toUserDto(userRepository.updateUser(userMapper.toUser(userUpdateDto), userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream().map(userMapper::toUserDto).toList();
    }

    @Override
    public UserDto getUser(Long userId) {
        return userMapper.toUserDto(userRepository.getUser(userId));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }
}