package gg.w6.chesslib.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FileTest {
    @Test
    void testCOUNT() {
        assertEquals(File.COUNT, File.values().length);
    }

    @Test
    void testToString() {
        assertEquals("d", File.D.toString());
    }
}
