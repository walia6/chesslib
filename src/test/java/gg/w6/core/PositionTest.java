package gg.w6.core;

import gg.w6.util.CastlingRights;
import gg.w6.util.Color;
import gg.w6.util.Coordinate;
import gg.w6.util.File;
import gg.w6.util.Rank;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PositionTest {

    @Test
    void testValidFENParsing() {
        final String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        final Position position = Position.valueOf(fen);

        assertEquals(Color.WHITE, position.getToMove());
        assertEquals(new CastlingRights(true, true, true, true), position.getCastlingRights());
        assertNull(position.getEnPassantTarget());
        assertEquals(0, position.getHalfMoveClock());
        assertEquals(1, position.getFullMoves());

        // Verify piece placement: A1 should be rook (R), E8 should be king (k)
        assertNotNull(position.getSquare(File.A, Rank.ONE).getPiece());
        assertNotNull(position.getSquare(File.E, Rank.EIGHT).getPiece());
    }

    @Test
    void testGenerateFENMatchesInput() {
        final String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        final Position position = Position.valueOf(fen);
        assertEquals(fen, position.generateFEN());
    }

    @Test
    void testFENWithEnPassantTarget() {
        final String fen = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
        final Position position = Position.valueOf(fen);
        assertEquals(Coordinate.valueOf("e3"), position.getEnPassantTarget());
        assertEquals(fen, position.generateFEN());
    }

    @Test
    void testInvalidFENTooFewFields() {
        final String badFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0";
        assertThrows(IllegalArgumentException.class, () -> Position.valueOf(badFEN));
    }

    @Test
    void testInvalidFENTooManyFields() {
        final String badFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 extra";
        assertThrows(IllegalArgumentException.class, () -> Position.valueOf(badFEN));
    }

    @Test
    void testInvalidCastlingField() {
        final String badFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KKq - 0 1";
        assertThrows(IllegalArgumentException.class, () -> Position.valueOf(badFEN));
    }

    @Test
    void testInvalidColorField() {
        final String badFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR x KQkq - 0 1";
        assertThrows(IllegalArgumentException.class, () -> Position.valueOf(badFEN));
    }

    @Test
    void testInvalidHalfMoveClock() {
        final String badFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - x 1";
        assertThrows(IllegalArgumentException.class, () -> Position.valueOf(badFEN));
    }

    @Test
    void testInvalidFullMoveCounter() {
        final String badFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 x";
        assertThrows(IllegalArgumentException.class, () -> Position.valueOf(badFEN));
    }

    @Test
    void testInvalidPiecePlacement() {
        // 9 squares in one rank = invalid
        final String badFEN = "rnbqkbnrr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        assertThrows(IllegalArgumentException.class, () -> Position.valueOf(badFEN));
    }

    @Test
    void testNoCastlingRights() {
        final String fen = "8/8/8/8/8/8/8/8 w - - 0 1";
        final Position position = Position.valueOf(fen);
        assertFalse(position.getCastlingRights().whiteKingside());
        assertFalse(position.getCastlingRights().whiteQueenside());
        assertFalse(position.getCastlingRights().blackKingside());
        assertFalse(position.getCastlingRights().blackQueenside());
        assertEquals("-", position.generateFEN().split(" ")[2]);
    }

    @Test
    void testGetSquareByCoordinate() {
        final String fen = "8/8/8/8/8/8/8/R7 w - - 0 1";
        final Position position = Position.valueOf(fen);
        final Coordinate a1 = Coordinate.valueOf(File.A, Rank.ONE);
        assertNotNull(position.getSquare(a1));
        assertNotNull(position.getSquare(a1).getPiece());
    }

    @Test
    void testGenerateFENWithAllCastlingCombinations() {
        // K only
        final String fenK = "8/8/8/8/8/8/8/R3K2R w K - 0 1";
        assertEquals("K", Position.valueOf(fenK).generateFEN().split(" ")[2]);

        // Q only
        final String fenQ = "8/8/8/8/8/8/8/R3K2R w Q - 0 1";
        assertEquals("Q", Position.valueOf(fenQ).generateFEN().split(" ")[2]);

        // k only
        final String fenk = "8/8/8/8/8/8/8/r3k2r w k - 0 1";
        assertEquals("k", Position.valueOf(fenk).generateFEN().split(" ")[2]);

        // q only
        final String fenq = "8/8/8/8/8/8/8/r3k2r w q - 0 1";
        assertEquals("q", Position.valueOf(fenq).generateFEN().split(" ")[2]);

        // All
        final String fenAll = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        assertEquals("KQkq", Position.valueOf(fenAll).generateFEN().split(" ")[2]);
    }

    @Test
    void testConstructorValidSquares() {
        final Square[][] squares = new Square[File.COUNT][Rank.COUNT];
        for (int fileIndex = 0; fileIndex < File.COUNT; fileIndex++) {
            for (int rankIndex = 0; rankIndex < Rank.COUNT; rankIndex++) {
                squares[fileIndex][rankIndex] = Square.valueOf(fileIndex,
                        rankIndex, null);
            }
        }
        
        assertDoesNotThrow(() -> new Position(squares, null, null, null, 0, 0));
    }

    @Test
    void testConstructorInvalidSquaresInvalidFileCount() {
        final Square[][] squares = new Square[File.COUNT - 1][Rank.COUNT];
        for (int fileIndex = 0; fileIndex < File.COUNT - 1; fileIndex++) {
            for (int rankIndex = 0; rankIndex < Rank.COUNT; rankIndex++) {
                squares[fileIndex][rankIndex] = Square.valueOf(fileIndex,
                        rankIndex, null);
            }
        }
        
        assertThrows(IllegalArgumentException.class,
                () -> new Position(squares, null, null, null, 0, 0));
    }

    @Test
    void testConstructorInvalidSquaresInvalidRankCount() {
        final Square[][] squares = new Square[File.COUNT][Rank.COUNT - 1];
        for (int fileIndex = 0; fileIndex < File.COUNT; fileIndex++) {
            for (int rankIndex = 0; rankIndex < Rank.COUNT - 1; rankIndex++) {
                squares[fileIndex][rankIndex] = Square.valueOf(fileIndex,
                        rankIndex, null);
            }
        }
        
        assertThrows(IllegalArgumentException.class,
                () -> new Position(squares, null, null, null, 0, 0));
    }

    @Test
    void testConstructorInvalidSquaresNullSquare() {
        final Square[][] squares = new Square[File.COUNT][Rank.COUNT];
        
        assertThrows(NullPointerException.class,
                () -> new Position(squares, null, null, null, 0, 0));
    }

    @Test
    void testConstructorInvalidSquaresInvalidCoordinates() {
        final Square[][] squares = new Square[File.COUNT][Rank.COUNT];
        for (int fileIndex = 0; fileIndex < File.COUNT; fileIndex++) {
            for (int rankIndex = 0; rankIndex < Rank.COUNT; rankIndex++) {
                squares[fileIndex][rankIndex] = Square.valueOf(fileIndex,
                        fileIndex, null);
            }
        }
        
        assertThrows(IllegalArgumentException.class,
                () -> new Position(squares, null, null, null, 0, 0));
    }

}
