package ru.practicum.shareit.item;

import lombok.Data;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private long itemCounter = 1;
    private final HashMap<Long, Item> itemStorage;

    @Override
    public Item createItem(Item item) {
        item.setId(itemCounter);
        itemCounter++;
        itemStorage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item updateItem) {
        if (!itemStorage.containsKey(updateItem.getId())) {
            throw new NotFoundException("Такой вещи нет в базе");
        }
        Item item = itemStorage.get(updateItem.getId());
        if (updateItem.getName() != null) {
            item.setName(updateItem.getName());
        }
        if (updateItem.getDescription() != null) {
            item.setDescription(updateItem.getDescription());
        }
        if (updateItem.getAvailable() != null) {
            item.setAvailable(updateItem.getAvailable());
        }
        itemStorage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItem(Long itemId) {
        return itemStorage.get(itemId);
    }

    @Override
    public List<Item> getUserItems(Long ownerId) {
        return itemStorage.values().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .toList();
    }

    @Override
    public List<Item> getSearchItems(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        List<Item> nameSearchItems = itemStorage.values()
                .stream()
                .filter(item -> item.getName().toLowerCase().contains(text))
                .filter(item -> item.getAvailable())
                .toList();
        List<Item> descriptionSearchItems = itemStorage.values()
                .stream()
                .filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(item -> item.getAvailable())
                .toList();
        return Stream.concat(nameSearchItems.stream(), descriptionSearchItems.stream())
                .distinct()
                .collect(Collectors.toList());
    }
}