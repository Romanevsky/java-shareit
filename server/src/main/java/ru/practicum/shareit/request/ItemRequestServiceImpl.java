package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ItemRequestMapper itemRequestMapper;
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final ItemRequestRepository itemRequestRepository;
    @Autowired
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto save(ItemRequestCreateDto itemRequestCreateDto, Long requestorId) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new DataNotFoundException("Такого пользователя нет в базе"));
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestCreateDto, requestor);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
        List<ItemDto> items = itemRepository
                .findByRequestId(itemRequestDto.getId())
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getUserItemRequests(Long requestorId) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new DataNotFoundException("Такого пользователя нет в базе"));
        return itemRequestRepository.findAllByRequestorId(requestorId)
                .stream()
                .map(itemRequestMapper::toItemRequestDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long requestorId) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new DataNotFoundException("Такого пользователя нет в базе"));
        return itemRequestRepository.findByRequestorIdNot(requestorId)
                .stream()
                .map(itemRequestMapper::toItemRequestDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .toList();
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestorId, Long requestId) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new DataNotFoundException("Такого пользователя нет в базе"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new DataNotFoundException("Такого запроса нет в базе"));
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        List<ItemDto> items = itemRepository
                .findByRequestId(itemRequestDto.getId())
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }
}
