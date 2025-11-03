package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    ItemDto save(ItemCreateDto itemCreateDto);

    ItemDto updateItem(ItemUpdateDto itemUpdateDto);

    ItemInfoDto getItem(Long itemId, Long userId);

    List<ItemInfoDto> getUserItems(Long ownerId);

    List<ItemDto> getSearchItems(String text);

    CommentDto saveComment(CommentCreateDto commentCreateDto);
}