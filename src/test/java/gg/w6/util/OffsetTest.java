package gg.w6.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;

public class OffsetTest {

    @Test
    void testValueOf() {
        Offset offset = Offset.valueOf(5, 3);
        assertEquals(5, offset.getFiles());
        assertEquals(3, offset.getRanks());
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

        assertNotEquals("Hello, world!", offset);
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

        assertEquals(Arrays.asList(expected), StreamSupport.stream(offset.extendFrom(coordinate, range).spliterator(), false)
    .collect(Collectors.toList()));

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

        assertEquals(Arrays.asList(expected), StreamSupport.stream(offset.extendFrom(coordinate, range).spliterator(), false)
        .collect(Collectors.toList()));
    }

}
