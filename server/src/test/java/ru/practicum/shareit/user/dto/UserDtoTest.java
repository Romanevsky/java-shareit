package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
class UserDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws Exception {
        UserDto userDto = new UserDto(1L, "Test", "test@mail.com");
        String json = objectMapper.writeValueAsString(userDto);
        assertThat(json).contains("\"id\":1", "\"name\":\"Test\"", "\"email\":\"test@mail.com\"");
    }

    @Test
    void testDeserialize() throws Exception {
        String json = "{\"id\":1,\"name\":\"Test\",\"email\":\"test@mail.com\"}";
        UserDto userDto = objectMapper.readValue(json, UserDto.class);
        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEqualTo("Test");
        assertThat(userDto.getEmail()).isEqualTo("test@mail.com");
    }
}