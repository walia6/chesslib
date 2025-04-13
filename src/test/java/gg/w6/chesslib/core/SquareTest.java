package gg.w6.chesslib.core;

import gg.w6.chesslib.piece.Piece;
import gg.w6.chesslib.util.Color;
import gg.w6.chesslib.util.Coordinate;
import gg.w6.chesslib.util.File;
import gg.w6.chesslib.util.Rank;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SquareTest {

    @Test
    void testConstructorWithCoordinate() {
        Coordinate coord = Coordinate.valueOf(File.B, Rank.TWO);
        Square square = new Square(coord);
        assertEquals(coord, square.getCoordinate());
        assertNull(square.getPiece());
    }

    @Test
    void testConstructorWithFileRank() {
        Square square = new Square(File.C, Rank.THREE);
        assertEquals(File.C, square.getCoordinate().getFile());
        assertEquals(Rank.THREE, square.getCoordinate().getRank());
        assertNull(square.getPiece());
    }

    @Test
    void testConstructorWithIndices() {
        Square square = new Square(3, 4); // File.D (3), Rank.FIVE (4)
        assertEquals(3, square.getCoordinate().getFile().ordinal());
        assertEquals(4, square.getCoordinate().getRank().ordinal());
        assertNull(square.getPiece());
    }

    @Test
    void testConstructorWithCoordinateAndPiece() {
        Coordinate coord = Coordinate.valueOf(File.E, Rank.FOUR);
        Piece piece = mock(Piece.class);
        Square square = new Square(coord, piece);
        assertEquals(coord, square.getCoordinate());
        assertEquals(piece, square.getPiece());
    }

    @Test
    void testGetPiece() {
        Piece piece = mock(Piece.class);
        Square square = Square.valueOf(0, 0, piece);

        assertEquals(piece, square.getPiece());
    }

    @Test
    void testGetColor() {
        // A1 should be BLACK (0 + 0 % 2 == 0)
        Square blackSquare = new Square(File.A, Rank.ONE);
        assertEquals(Color.BLACK, blackSquare.getColor());

        // B1 should be WHITE (1 + 0 % 2 == 1)
        Square whiteSquare = new Square(File.B, Rank.ONE);
        assertEquals(Color.WHITE, whiteSquare.getColor());
    }
}
