package ru.practicum.shareit.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HeaderTest {

    public static final String USER_ID = "X-Sharer-User-Id";
    String userId = "X-Sharer-User-Id";

    @Test
    void shouldGetUserId() {
        assertEquals(USER_ID, userId);
    }
}
