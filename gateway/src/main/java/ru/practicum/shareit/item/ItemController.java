package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collections;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Start creating item: {}", itemCreateDto);
        return itemClient.createItem(ownerId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                             @PathVariable @Positive Long itemId,
                                             @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("Start updating item: {}", itemUpdateDto);
        return itemClient.updateItem(userId, itemId, itemUpdateDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Start getting item: {}", itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Start getting user {} items}", ownerId);
        return itemClient.getItemsByUserId(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearchItems(@RequestParam("text") String text) {
        log.info("Start getting items with words {}", text);

        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return itemClient.searchItems(text.trim());
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") @Positive Long authorId,
                                                @PathVariable @Positive Long itemId,
                                                @RequestBody @Valid CommentCreateDto commentCreateDto) {
        log.info("Start creating comment {}}", commentCreateDto);
        commentCreateDto.setItemId(itemId);
        commentCreateDto.setAuthorId(authorId);
        return itemClient.createComment(commentCreateDto);
    }
}