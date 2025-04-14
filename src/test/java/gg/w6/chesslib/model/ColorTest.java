package gg.w6.chesslib.model;

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
        assertEquals(Color.WHITE, Color.valueOf('W'));
        assertEquals(Color.WHITE, Color.valueOf('w'));
    }

    @Test
    void TestValueOfB() {
        assertEquals(Color.BLACK, Color.valueOf('B'));
        assertEquals(Color.BLACK, Color.valueOf('b'));
    }
}
