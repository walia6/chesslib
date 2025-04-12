package gg.w6.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CoordinateTest {

    @Test
    void testValueOfFileRank() {
        File file = File.B;
        Rank rank = Rank.FIVE;
        Coordinate coordinate = Coordinate.valueOf(file, rank);
        assertEquals(file, coordinate.getFile());
        assertEquals(rank, coordinate.getRank());
    }

    @Test
    void testValueOfIntIntFileOutOfBoundsGt() {
        assertThrows(IllegalArgumentException.class, () -> Coordinate.valueOf(File.COUNT, 3));

    }

    @Test
    void testValueOfIntIntFileOutOfBoundsLt() {
        assertThrows(IllegalArgumentException.class, () -> Coordinate.valueOf(-7, 3));

    }

    @Test
    void testValueOfIntIntRankOutOfBoundsGt() {
        assertThrows(IllegalArgumentException.class, () -> Coordinate.valueOf(3, Rank.COUNT));

    }

    @Test
    void testValueOfIntIntRankOutOfBoundsLt() {
        assertThrows(IllegalArgumentException.class, () -> Coordinate.valueOf(3, -7));

    }

    @Test
    void testValueOfIntInt() {
        Coordinate coordinate = Coordinate.valueOf(3, 5);
        assertEquals(coordinate.getFile(), File.D);
        assertEquals(coordinate.getRank(), Rank.SIX);
    }

    @Test
    void testValueOfIntIntCacheHit() {
        Coordinate coordinate1 = Coordinate.valueOf(3, 5);
        Coordinate coordinate2 = Coordinate.valueOf(3, 5);
        assertSame(coordinate1, coordinate2);
    }

    @Test
    void testValueOfStringMalformed() {
        assertThrows(IllegalArgumentException.class, () -> Coordinate.valueOf("Malformed Coordinate"));
    }

    @Test
    void testValueOfString() {
        Coordinate coordinate = Coordinate.valueOf("g5");
        assertEquals(coordinate.getFile(), File.G);
        assertEquals(coordinate.getRank(), Rank.FIVE);
    }

    @Test
    void testToString() {
        Coordinate coordinate = Coordinate.valueOf(File.H, Rank.SEVEN);
        assertEquals(coordinate.toString(), "h7");
    }

    @Test
    void testEqualsTrueCoordinate() {
        Coordinate coordinate1 = Coordinate.valueOf("b3");
        Coordinate coordinate2 = Coordinate.valueOf("b3");
        assertEquals(coordinate1, coordinate2);
    }

    @Test
    void testEqualsFalseCoordinate() {
        Coordinate coordinate1 = Coordinate.valueOf("h7");
        Coordinate coordinate2 = Coordinate.valueOf("h3");
        
        Coordinate coordinate3 = Coordinate.valueOf("a2");
        Coordinate coordinate4 = Coordinate.valueOf("g4");

        assertNotEquals(coordinate1, coordinate2);
        assertNotEquals(coordinate3, coordinate4);
    }

    @Test
    void testEqualsFalseString() {
        Coordinate coordinate = Coordinate.valueOf("a1");
        assertNotEquals(coordinate, "Hello, world!");
    }



}
