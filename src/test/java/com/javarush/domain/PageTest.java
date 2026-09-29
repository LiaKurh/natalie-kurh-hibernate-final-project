package com.javarush.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PageTest {
    @Test
    @DisplayName("Successful creation of Page object with valid data")
    void should_CreatePageWithValidData() {
        Page page = new Page(10, 500);
        assertEquals(10, page.offset(), "Offset should be 10");
        assertEquals(500, page.limit(), "Limit should be 500");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -10, -100})
    @DisplayName("Should throw IllegalArgumentException when negative offset is passed")
    void should_ThrowIllegalArgumentException_When_OffsetIsNegative(int invalidOffset) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Page(invalidOffset, 500));
        assertEquals("Offset can't be negative!", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -50})
    @DisplayName("Should throw IllegalArgumentException when zero or negative limit is passed")
    void should_ThrowIllegalArgumentException_When_LimitIsZeroOrNegative(int invalidLimit) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Page(0, invalidLimit));
        assertEquals("Limit can't be negative!", exception.getMessage());
    }
}
