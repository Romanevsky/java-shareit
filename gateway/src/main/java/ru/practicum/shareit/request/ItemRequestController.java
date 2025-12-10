package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                                    @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Start creating item request: {}", itemRequestCreateDto);
        return itemRequestClient.createItemRequest(itemRequestCreateDto, requestorId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Start getting user {} item requests", requestorId);
        return itemRequestClient.getUserItemRequests(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Start getting all item requests for user {}", requestorId);
        return itemRequestClient.getAllItemRequests(requestorId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                                     @PathVariable Long requestId) {
        log.info("Start getting item request {} for user {}", requestorId);
        return itemRequestClient.getItemRequestById(requestorId, requestId);
    }
}
