package ru.practicum.shareit.item;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoTest {
    private final LocalDateTime current = LocalDateTime.now();
    private final ItemDto dto = new ItemDto(1L, "name", "description", true, 1L);
    @Autowired
    private JacksonTester<ItemDto> jacksonTester;

    @Test
    void test_serialize() throws Exception {
        JsonContent<ItemDto> result = jacksonTester.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.available").isEqualTo(true);
    }

    @Test
    void test_deserialize() throws IOException {
        final String dtoJson = "{   \n" +
                "    \"id\": 1,\n" +
                "    \"name\" : \"name\",\n" +
                "    \"description\" : \"description\",\n" +
                "    \"available\":true,\n" +
                "    \"requestId\": 1\n" +
                "}";

        var dtoTest = jacksonTester.parseObject(dtoJson);

        AssertionsForClassTypes.assertThat(dtoTest).extracting("id").isEqualTo(dto.getId());
        AssertionsForClassTypes.assertThat(dtoTest).extracting("name").isEqualTo(dto.getName());
        AssertionsForClassTypes.assertThat(dtoTest).extracting("description")
                .isEqualTo(dto.getDescription());
        AssertionsForClassTypes.assertThat(dtoTest).extracting("available").isEqualTo(dto.getAvailable());
        AssertionsForClassTypes.assertThat(dtoTest).extracting("requestId").isEqualTo(dto.getRequestId());
    }
}
