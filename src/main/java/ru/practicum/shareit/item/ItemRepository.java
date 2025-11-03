package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item createItem(Item item);

    Item updateItem(Item item);

    Item getItem(Long itemId);

    List<Item> getUserItems(Long ownerId);

    List<Item> getSearchItems(String text);
}