package gg.w6.core;

import gg.w6.util.CastlingRights;
import gg.w6.util.Color;
import gg.w6.util.Coordinate;
import gg.w6.util.File;
import gg.w6.util.MoveGenerator;
import gg.w6.util.Rank;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.function.Supplier;

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

    @Test
    void testGod() {
        String starting  = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        class  StringTuple {
            private final String x;
            private final String y;

            StringTuple(String x, String y) {
                this.x = x;
                this.y = y;
            }

            String getX() {
                return x;
            }

            String getY() {
                return y;
            }
        }

        List<StringTuple> moveToResultingPosition = List.of(
            new StringTuple("e2-e4", "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"),
            new StringTuple("c7-c6", "rnbqkbnr/pp1ppppp/2p5/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2"),
            new StringTuple("d2-d4", "rnbqkbnr/pp1ppppp/2p5/8/3PP3/8/PPP2PPP/RNBQKBNR b KQkq d3 0 2"),
            new StringTuple("d7-d5", "rnbqkbnr/pp2pppp/2p5/3p4/3PP3/8/PPP2PPP/RNBQKBNR w KQkq d6 0 3"),
            new StringTuple("e4-e5", "rnbqkbnr/pp2pppp/2p5/3pP3/3P4/8/PPP2PPP/RNBQKBNR b KQkq - 0 3"),
            new StringTuple("c6-c5", "rnbqkbnr/pp2pppp/8/2ppP3/3P4/8/PPP2PPP/RNBQKBNR w KQkq - 0 4"),
            new StringTuple("Ng1-f3", "rnbqkbnr/pp2pppp/8/2ppP3/3P4/5N2/PPP2PPP/RNBQKB1R b KQkq - 1 4"),
            new StringTuple("Bc8-g4", "rn1qkbnr/pp2pppp/8/2ppP3/3P2b1/5N2/PPP2PPP/RNBQKB1R w KQkq - 2 5"),
            new StringTuple("Bf1-b5", "rn1qkbnr/pp2pppp/8/1BppP3/3P2b1/5N2/PPP2PPP/RNBQK2R b KQkq - 3 5"),
            new StringTuple("Nb8-c6", "r2qkbnr/pp2pppp/2n5/1BppP3/3P2b1/5N2/PPP2PPP/RNBQK2R w KQkq - 4 6"),
            new StringTuple("h2-h3", "r2qkbnr/pp2pppp/2n5/1BppP3/3P2b1/5N1P/PPP2PP1/RNBQK2R b KQkq - 0 6"),
            new StringTuple("Bg4xf3", "r2qkbnr/pp2pppp/2n5/1BppP3/3P4/5b1P/PPP2PP1/RNBQK2R w KQkq - 0 7"),
            new StringTuple("Qd1xf3", "r2qkbnr/pp2pppp/2n5/1BppP3/3P4/5Q1P/PPP2PP1/RNB1K2R b KQkq - 0 7"),
            new StringTuple("e7-e6", "r2qkbnr/pp3ppp/2n1p3/1BppP3/3P4/5Q1P/PPP2PP1/RNB1K2R w KQkq - 0 8"),
            new StringTuple("e1-g1", "r2qkbnr/pp3ppp/2n1p3/1BppP3/3P4/5Q1P/PPP2PP1/RNB2RK1 b kq - 1 8"),
            new StringTuple("Qd8-b6", "r3kbnr/pp3ppp/1qn1p3/1BppP3/3P4/5Q1P/PPP2PP1/RNB2RK1 w kq - 2 9"),
            new StringTuple("Bb5xc6", "r3kbnr/pp3ppp/1qB1p3/2ppP3/3P4/5Q1P/PPP2PP1/RNB2RK1 b kq - 0 9"),
            new StringTuple("b7xc6", "r3kbnr/p4ppp/1qp1p3/2ppP3/3P4/5Q1P/PPP2PP1/RNB2RK1 w kq - 0 10"),
            new StringTuple("c2-c3", "r3kbnr/p4ppp/1qp1p3/2ppP3/3P4/2P2Q1P/PP3PP1/RNB2RK1 b kq - 0 10"),
            new StringTuple("e8-c8", "2kr1bnr/p4ppp/1qp1p3/2ppP3/3P4/2P2Q1P/PP3PP1/RNB2RK1 w - - 1 11"),
            new StringTuple("Bc1-e3", "2kr1bnr/p4ppp/1qp1p3/2ppP3/3P4/2P1BQ1P/PP3PP1/RN3RK1 b - - 2 11"),
            new StringTuple("c5-c4", "2kr1bnr/p4ppp/1qp1p3/3pP3/2pP4/2P1BQ1P/PP3PP1/RN3RK1 w - - 0 12"),
            new StringTuple("b2-b4", "2kr1bnr/p4ppp/1qp1p3/3pP3/1PpP4/2P1BQ1P/P4PP1/RN3RK1 b - b3 0 12"),
            new StringTuple("c4xb3", "2kr1bnr/p4ppp/1qp1p3/3pP3/3P4/1pP1BQ1P/P4PP1/RN3RK1 w - - 0 13")
        );

        Position position = Position.valueOf(starting);

        for (StringTuple stringTuple : moveToResultingPosition) {
            final Move move = MoveGenerator.generateMoveFromString(position, stringTuple.getX());
            position = position.applyTo(move);
            assertEquals(stringTuple.getY(), position.generateFEN(), new Supplier<String>() {

                @Override
                public String get() {

                    return stringTuple.getX();
                }
                
            });
        }

    }

}
