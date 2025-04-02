package gg.w6.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ColorTest {
    @Test
    void TestValueOfMalformed() {
        assertThrows(IllegalArgumentException.class, () -> Color.valueOf('J'));
    }

    @Test
    void TestValueOfW() {
        assertEquals(Color.valueOf('W'), Color.WHITE);
        assertEquals(Color.valueOf('w'), Color.WHITE);
    }

    @Test
    void TestValueOfB() {
        assertEquals(Color.valueOf('B'), Color.BLACK);
        assertEquals(Color.valueOf('b'), Color.BLACK);
    }
}
