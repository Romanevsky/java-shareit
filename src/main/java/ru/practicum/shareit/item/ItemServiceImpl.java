package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final ItemMapper itemMapper;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final UserMapper userMapper;

    @Override
    public ItemDto createItem(ItemCreateDto itemCreateDto) {
        User user = userRepository.getUser(itemCreateDto.getOwnerId());
        if (user == null) {
            throw new NotFoundException("Такого пользователя нет в базе");
        }
        return itemMapper.toItemDto(itemRepository.createItem(itemMapper.toItem(itemCreateDto, user)));
    }

    @Override
    public ItemDto updateItem(ItemUpdateDto itemUpdateDto) {
        if (itemRepository.getItem(itemUpdateDto.getId()).getOwner().getId() != itemUpdateDto.getOwnerId()) {
            throw new NotFoundException("Изменить запись вещи  может только её владелец");
        }
        Item item = itemMapper.toItem(itemUpdateDto);
        item.setOwner(userRepository.getUser(itemUpdateDto.getOwnerId()));
        return itemMapper.toItemDto(itemRepository.updateItem(item));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return itemMapper.toItemDto(itemRepository.getItem(itemId));
    }

    @Override
    public List<ItemDto> getUserItems(Long ownerId) {
        return itemRepository.getUserItems(ownerId).stream().map(itemMapper::toItemDto).toList();
    }

    @Override
    public List<ItemDto> getSearchItems(String text) {
        return itemRepository.getSearchItems(text).stream().map(itemMapper::toItemDto).toList();
    }
}