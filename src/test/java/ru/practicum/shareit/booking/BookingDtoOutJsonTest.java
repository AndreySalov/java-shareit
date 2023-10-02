package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoOutJsonTest {
    private final LocalDateTime current = LocalDateTime.now();
    private final ItemDto itemDto = new ItemDto(1L, "item1", "descript", true, null);
    private final UserDto userDto = new UserDto(1L, "user1", "test@test");
    private final BookingDtoOut bookingDto = new BookingDtoOut(1L, current, current.plusHours(1), itemDto, userDto, BookingStatus.APPROVED);
    @Autowired
    private JacksonTester<BookingDtoOut> jacksonTester;

    @Test
    void testSerialize() throws Exception {
        var result = jacksonTester.write(bookingDto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.booker");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingDto.getStatus().toString());
        assertThat(result).extractingJsonPathValue("$.item")
                .extracting("id").isEqualTo(bookingDto.getItem().getId().intValue());
        assertThat(result).extractingJsonPathValue("$.item")
                .extracting("name").isEqualTo(bookingDto.getItem().getName());
        assertThat(result).extractingJsonPathValue("$.booker")
                .extracting("id").isEqualTo(bookingDto.getBooker().getId().intValue());
        assertThat(result).extractingJsonPathValue("$.booker")
                .extracting("email").isEqualTo(bookingDto.getBooker().getEmail());

    }
}
