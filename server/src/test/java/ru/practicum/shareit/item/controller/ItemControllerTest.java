package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    ItemInfoDto itemInfoDto;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper objectMapper;

    private ItemCreateDto itemCreateDto;
    private ItemDto itemDto;
    private ItemUpdateDto itemUpdateDto;
    private ItemDto updatedDto;
    private CommentCreateDto commentCreateDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemCreateDto = new ItemCreateDto(null, "Test item", "Description", true, null);
        itemDto = new ItemDto(1L, "Test Item", "Description", true);
        itemUpdateDto = new ItemUpdateDto(1L, 1L, "Test item update", "Update Descroption", false, null);
        updatedDto = new ItemDto(1L, "Test item update", "Update Description", false);
        itemInfoDto = new ItemInfoDto(1L, "Test Item", "Description", false, null, null, null);
        commentCreateDto = new CommentCreateDto("Test comment", 1L, 1L);
        commentDto = new CommentDto(1L, "Test comment", "Test User", null);
    }

    @Test
    void createItem() throws Exception {
        given(itemService.save(any(ItemCreateDto.class))).willReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @Test
    void updateItem() throws Exception {
        given(itemService.updateItem(any(ItemUpdateDto.class))).willReturn(updatedDto);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedDto.getName()));
    }

    @Test
    void getItem() throws Exception {
        given(itemService.getItem(anyLong(), anyLong())).willReturn(itemInfoDto);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemInfoDto.getName()));

    }

    @Test
    void getUserItems() throws Exception {
        given(itemService.getUserItems(anyLong())).willReturn(Collections.singletonList(itemInfoDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemInfoDto.getId()));
    }

    @Test
    void getSearchItems() throws Exception {
        given(itemService.getSearchItems(any())).willReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));
    }

    @Test
    void createComment() throws Exception {
        given(itemService.saveComment(any(CommentCreateDto.class))).willReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(commentDto.getText()));
    }
}