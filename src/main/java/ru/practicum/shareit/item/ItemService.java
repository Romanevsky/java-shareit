package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemCreateDto itemCreateDto);

    ItemDto updateItem(ItemUpdateDto itemUpdateDto);

    ItemDto getItem(Long itemId);

    List<ItemDto> getUserItems(Long ownerId);

    List<ItemDto> getSearchItems(String text);
}