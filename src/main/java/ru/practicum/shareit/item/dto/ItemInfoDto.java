package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDatesDto;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemInfoDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private BookingDatesDto lastBooking;
    private BookingDatesDto nextBooking;
    private List<CommentDto> comments;
}