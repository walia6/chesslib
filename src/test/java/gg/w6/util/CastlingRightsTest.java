package gg.w6.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class CastlingRightsTest {

    private static void assertRights(CastlingRights castlingRights,
            boolean whiteKingside, boolean whiteQueenside,
            boolean blackKingside, boolean blackQueenside) {
        assertEquals(castlingRights.whiteKingside(), whiteKingside);
        assertEquals(castlingRights.whiteQueenside(), whiteQueenside);
        assertEquals(castlingRights.blackKingside(), blackKingside);
        assertEquals(castlingRights.blackQueenside(), blackQueenside);
    }

    @Test
    void testConstructorEmpty() {
        CastlingRights castlingRights = new CastlingRights();
        assertRights(castlingRights, true, true, true, true);
    }

    @Test
    void testConstructorBool() {
        CastlingRights castlingRightsTrue = new CastlingRights(true);
        CastlingRights castlingRightsFalse = new CastlingRights(false);
        assertRights(castlingRightsTrue, true, true, true, true);
        assertRights(castlingRightsFalse, false, false, false, false);
    }

    @Test
    void testConstructorBoolBoolBoolBool() {
        for (int i = 0; i < 16; i++) {
            boolean[] b = new boolean[4];
            for (int j = 0; j < 4; j++) {
                b[j] = ((i >> j) & 1) == 1;
            }
            assertRights(new CastlingRights(b[0], b[1], b[2], b[3]), b[0], b[1],b[2], b[3]);
        }
    }

    @Test
    void testWhiteKingside() {
        for (int i = 0; i < 16; i++) {
            boolean[] b = new boolean[4];
            for (int j = 0; j < 4; j++) {
                b[j] = ((i >> j) & 1) == 1;
            }
            CastlingRights castlingRights = new CastlingRights(b[0], b[1], b[2], b[3]);
            assertEquals(castlingRights.whiteKingside(), b[0]);
        }
    }

    @Test
    void testWhiteQueenside() {
        for (int i = 0; i < 16; i++) {
            boolean[] b = new boolean[4];
            for (int j = 0; j < 4; j++) {
                b[j] = ((i >> j) & 1) == 1;
            }
            CastlingRights castlingRights = new CastlingRights(b[0], b[1], b[2], b[3]);
            assertEquals(castlingRights.whiteQueenside(), b[1]);
        }
    }

    @Test
    void testBlackKingside() {
        for (int i = 0; i < 16; i++) {
            boolean[] b = new boolean[4];
            for (int j = 0; j < 4; j++) {
                b[j] = ((i >> j) & 1) == 1;
            }
            CastlingRights castlingRights = new CastlingRights(b[0], b[1], b[2], b[3]);
            assertEquals(castlingRights.blackKingside(), b[2]);
        }
    }

    @Test
    void testBlackQueenside() {
        for (int i = 0; i < 16; i++) {
            boolean[] b = new boolean[4];
            for (int j = 0; j < 4; j++) {
                b[j] = ((i >> j) & 1) == 1;
            }
            CastlingRights castlingRights = new CastlingRights(b[0], b[1], b[2], b[3]);
            assertEquals(castlingRights.blackQueenside(), b[3]);
        }
    }

    @Test
    void testEqualsTrueCastlingRights() {
        for (int i = 0; i < 16; i++) {
            boolean[] b = new boolean[4];
            for (int j = 0; j < 4; j++) {
                b[j] = ((i >> j) & 1) == 1;
            }
            CastlingRights castlingRights1 = new CastlingRights(b[0], b[1], b[2], b[3]);
            CastlingRights castlingRights2 = new CastlingRights(b[0], b[1], b[2], b[3]);

            assertEquals(castlingRights1, castlingRights2);
        }
    }

    @Test
    void testEqualsFalseCastlingRights() {
        for (int i = 0; i < 16; i++) {
            boolean[] b = new boolean[4];
            for (int j = 0; j < 4; j++) {
                b[j] = ((i >> j) & 1) == 1;
            }
            CastlingRights castlingRights = new CastlingRights(b[0], b[1], b[2], b[3]);
            CastlingRights castlingRights0 = new CastlingRights(!b[0], b[1], b[2], b[3]);
            CastlingRights castlingRights1 = new CastlingRights(b[0], !b[1], b[2], b[3]);
            CastlingRights castlingRights2 = new CastlingRights(b[0], b[1], !b[2], b[3]);
            CastlingRights castlingRights3 = new CastlingRights(b[0], b[1], b[2], !b[3]);

            assertNotEquals(castlingRights, castlingRights0);
            assertNotEquals(castlingRights, castlingRights1);
            assertNotEquals(castlingRights, castlingRights2);
            assertNotEquals(castlingRights, castlingRights3);

        }
    }

    @Test
    void testEqualsFalseString() {
        CastlingRights castlingRights = new CastlingRights();
        assertNotEquals(castlingRights, "Hello, world!");
    }
}
