package gg.w6.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FileTest {
    @Test
    void testCOUNT() {
        assertEquals(File.COUNT, File.values().length);
    }

    @Test
    void testToString() {
        assertEquals(File.D.toString(), "d");
    }
}
