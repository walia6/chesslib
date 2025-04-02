package gg.w6.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OffsetTest {
    @BeforeEach
    private void resetCache() {
        Offset.resetCache();
    }

    @Test
    void testValueOf() {
        Offset offset = Offset.valueOf(5, 3);
        assertEquals(offset.getFiles(), 5);
        assertEquals(offset.getRanks(), 3);
    }
    
    @Test
    static void testValueOfCacheHit() {
        Offset offset1 = Offset.valueOf(3, 2);
        Offset offset2 = Offset.valueOf(3, 2);
        assertSame(offset1, offset2);
    }

    @Test
    void testApplyTo() {
        Coordinate coordinate = Coordinate.valueOf("f6");
        Offset offset = Offset.valueOf(2, -4);

        assertEquals(offset.applyTo(coordinate), Coordinate.valueOf("h2"));
    }

    @Test
    void testApplyToFileOutOfBoundsGt() {
        Coordinate coordinate = Coordinate.valueOf("f6");
        Offset offset = Offset.valueOf(2, 3);

        assertNull(offset.applyTo(coordinate));
    }

    @Test
    void testApplyToRankOutOfBoundsGt() {
        Coordinate coordinate = Coordinate.valueOf("f6");
        Offset offset = Offset.valueOf(3, -4);

        assertNull(offset.applyTo(coordinate));
    }

    @Test
    void testApplyToFileOutOfBoundsLt() {
        Coordinate coordinate = Coordinate.valueOf("f6");
        Offset offset = Offset.valueOf(-6, -4);

        assertNull(offset.applyTo(coordinate));
    }

    @Test
    void testApplyToRankOutOfBoundsLt() {
        Coordinate coordinate = Coordinate.valueOf("f6");
        Offset offset = Offset.valueOf(2, -6);

        assertNull(offset.applyTo(coordinate));
    }

    @Test
    void testEqualsTrueOffset() {
        Offset offset1 = Offset.valueOf(2, -4);
        Offset offset2 = Offset.valueOf(2, -4);

        assertEquals(offset1, offset2);
    }

    @Test
    void testEqualsFalseOffset() {
        Offset offset1 = Offset.valueOf(2, -4);
        Offset offset2 = Offset.valueOf(2, -3);
        Offset offset3 = Offset.valueOf(2, -4);
        Offset offset4 = Offset.valueOf(3, -4);

        assertNotEquals(offset1, offset2);
        assertNotEquals(offset3, offset4);
    }

    @Test
    void testEqualsFalseString() {
        Offset offset = Offset.valueOf(2, -4);

        assertNotEquals(offset, "Hello, world!");
    }

    @Test
    void testExtendFromInfinite() {
        Coordinate coordinate = Coordinate.valueOf("b1");
        Offset offset = Offset.valueOf(1, 2);
        int range = Integer.MAX_VALUE;
        Coordinate[] expected = {
            Coordinate.valueOf("c3"),
            Coordinate.valueOf("d5"),
            Coordinate.valueOf("e7")
        };

        assertEquals(offset.extendFrom(coordinate, range), Arrays.asList(expected));

    }

    @Test
    void testExtendFromFinite() {
        Coordinate coordinate = Coordinate.valueOf("b1");
        Offset offset = Offset.valueOf(1, 2);
        int range = 2;
        Coordinate[] expected = {
            Coordinate.valueOf("c3"),
            Coordinate.valueOf("d5")
        };

        assertEquals(offset.extendFrom(coordinate, range), Arrays.asList(expected));
    }

}
