package ru.practicum.shareit.item.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentCreateDto {
    @NotNull
    @NotBlank
    private String text;
    private Long itemId;
    private Long authorId;
}
