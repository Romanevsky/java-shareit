package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.DataIsNotAvailableException;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final ItemMapper itemMapper;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final UserMapper userMapper;
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final CommentRepository commentRepository;
    @Autowired
    private final CommentMapper commentMapper;
    @Autowired
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto save(ItemCreateDto itemCreateDto) {
        User user = userRepository.findById(itemCreateDto.getOwnerId())
                .orElseThrow(() -> new DataNotFoundException("Такого пользователя нет в базе"));
        Item item = itemMapper.toItem(itemCreateDto, user);
        if (itemCreateDto.getRequestId() != null) {
            item.setRequest(itemRequestRepository.findById(itemCreateDto.getRequestId())
                    .orElseThrow(() -> new DataNotFoundException("Такого запроса на вещь нет в базе")));
        }
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemUpdateDto itemUpdateDto) {
        User user = userRepository.findById(itemUpdateDto.getOwnerId())
                .orElseThrow(() -> new DataNotFoundException("Такого пользователя нет в базе"));
        Item item = itemRepository.findById(itemUpdateDto.getId())
                .orElseThrow(() -> new DataNotFoundException("Такой вещи нет в базе"));
        if (item.getOwner().getId() != itemUpdateDto.getOwnerId()) {
            throw new DataNotFoundException("Изменить запись вещи  может только её владелец");
        }
        if (itemUpdateDto.getName() != null) {
            item.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            item.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }
        if (itemUpdateDto.getItemRequestId() != null) {
            itemRequestRepository.findById(itemUpdateDto.getItemRequestId())
                    .orElseThrow(() -> new DataNotFoundException("Такого запроса на вещь нет в базе"));
        }
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemInfoDto getItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Такой вещи нет в базе"));
        List<Booking> bookingList = bookingRepository.findByItemId(itemId);
        ItemInfoDto itemInfoDto = prepareItemInfo(item, bookingList);
        if (item.getOwner().getId() != userId) {
            itemInfoDto.setLastBooking(null);
            itemInfoDto.setNextBooking(null);
        }
        return itemInfoDto;
    }

    @Override
    public List<ItemInfoDto> getUserItems(Long ownerId) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new DataNotFoundException("Такого пользователя нет в базе"));
        List<Item> itemList = itemRepository.findByOwnerId(ownerId);
        List<Booking> bookingList = bookingRepository.findByItemOwnerId(ownerId);
        List<ItemInfoDto> itemInfoDtoList = new ArrayList<>();
        for (Item item : itemList) {
            itemInfoDtoList.add(prepareItemInfo(item, bookingList));
        }
        return itemInfoDtoList;
    }

    @Override
    public List<ItemDto> getSearchItems(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findByText(text).stream().map(itemMapper::toItemDto).toList();
    }

    @Override
    public CommentDto saveComment(CommentCreateDto commentCreateDto) {
        List<Booking> booking = bookingRepository
                .findByItemIdAndBookerId(commentCreateDto.getItemId(), commentCreateDto.getAuthorId())
                .stream()
                .sorted(Comparator
                        .comparing(Booking::getEnd)
                        .reversed())
                .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                .toList();

        if (booking.isEmpty()) {
            throw new DataIsNotAvailableException("У данного пользователя нет завершенных бронирований этой вещи");
        }

        User user = userRepository.findById(commentCreateDto.getAuthorId())
                .orElseThrow(() -> new DataNotFoundException("Такого пользователя нет в базе"));
        Item item = itemRepository.findById(commentCreateDto.getItemId())
                .orElseThrow(() -> new DataNotFoundException("Такой вещи нет в базе"));

        Comment comment = commentMapper.toComment(commentCreateDto, user, item);
        comment.setCreated(LocalDateTime.now());

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    public ItemInfoDto prepareItemInfo(Item item, List<Booking> bookingList) {
        Optional<Booking> latestBooking = bookingList
                .stream()
                .filter(b -> b.getItem().getId() == item.getId())
                .sorted(Comparator
                        .comparing(Booking::getEnd)
                        .reversed())
                .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                .findFirst();
        Optional<Booking> closestBooking = bookingList
                .stream()
                .filter(b -> b.getItem().getId() == item.getId())
                .sorted(Comparator
                        .comparing(Booking::getStart))
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .findFirst();
        List<CommentDto> comments = commentRepository.findByItemId(item.getId())
                .stream()
                .map(commentMapper::toCommentDto)
                .toList();
        return itemMapper.toItemForInfoDto(item,
                latestBooking.orElse(new Booking()),
                closestBooking.orElse(new Booking()),
                comments);
    }
}