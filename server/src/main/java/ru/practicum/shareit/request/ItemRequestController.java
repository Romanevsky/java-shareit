package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    @Autowired
    private final ItemRequestService itemRequestService;


    @PostMapping
    public ItemRequestDto createItemRequest(@RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                            @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Start creating item request: {}", itemRequestCreateDto);
        ItemRequestDto itemRequestDto = itemRequestService.save(itemRequestCreateDto, requestorId);
        return itemRequestDto;
    }

    @GetMapping
    public List<ItemRequestDto> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Start getting user {} item requests", requestorId);
        return itemRequestService.getUserItemRequests(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Start getting all item requests for user {}", requestorId);
        return itemRequestService.getAllItemRequests(requestorId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                             @PathVariable Long requestId) {
        log.info("Start getting item request {} for user {}", requestorId);
        return itemRequestService.getItemRequestById(requestorId, requestId);
    }
}