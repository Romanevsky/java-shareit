package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Start creating item: {}", itemCreateDto);
        itemCreateDto.setOwnerId(ownerId);
        ItemDto item = itemService.save(itemCreateDto);
        log.info("Finish creating item: {}", itemCreateDto);
        return item;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @PathVariable Long itemId,
                              @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("Start updating item: {}", itemUpdateDto);
        itemUpdateDto.setOwnerId(ownerId);
        itemUpdateDto.setId(itemId);
        ItemDto item = itemService.updateItem(itemUpdateDto);
        log.info("Finish updating item: {}", itemUpdateDto);
        return item;
    }

    @GetMapping("/{itemId}")
    public ItemInfoDto getItem(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Start getting item: {}", itemId);
        ItemInfoDto item = itemService.getItem(itemId, userId);
        log.info("Finish getting item: {}", itemId);
        return item;
    }

    @GetMapping
    public List<ItemInfoDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Start getting user {} items}", ownerId);
        List<ItemInfoDto> userItems = itemService.getUserItems(ownerId);
        log.info("Finish getting user {} items}", ownerId);
        return userItems;
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchItems(@RequestParam("text") String text) {
        log.info("Start getting items with words {}}", text);
        List<ItemDto> searchItems = itemService.getSearchItems(text);
        log.info("Finish getting items with words {}}", text);
        return searchItems;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long authorId,
                                    @PathVariable Long itemId,
                                    @RequestBody CommentCreateDto commentCreateDto) {
        commentCreateDto.setItemId(itemId);
        commentCreateDto.setAuthorId(authorId);
        log.info("Start creating comment {}}", commentCreateDto);
        return itemService.saveComment(commentCreateDto);
    }
}