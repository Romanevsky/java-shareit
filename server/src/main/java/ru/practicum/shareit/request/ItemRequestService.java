package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto save(ItemRequestCreateDto itemRequestCreateDto, Long requestorId);

    List<ItemRequestDto> getUserItemRequests(Long requestorId);

    List<ItemRequestDto> getAllItemRequests(Long requestorId);

    ItemRequestDto getItemRequestById(Long requestorId, Long requestId);
}
