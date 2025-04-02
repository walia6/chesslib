package gg.w6.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RankTest {
    @Test
    void testCOUNT() {
        assertEquals(Rank.COUNT, Rank.values().length);
    }

    @Test
    void testToString() {
        assertEquals(Rank.SEVEN.toString(), "7");
    }
}
