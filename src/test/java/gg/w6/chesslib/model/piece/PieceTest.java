package gg.w6.chesslib.model.piece;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import gg.w6.chesslib.model.Color;

public class PieceTest {

    static final class TestPiece1 extends Piece {

        public TestPiece1(Color color) {
            super(color);
        }

        @Override
        char getWhiteLetter() {
            return 'X';
        }
    }

    static final class TestPiece2 extends Piece {

        public TestPiece2(Color color) {
            super(color);
        }

        @Override
        char getWhiteLetter() {
            return 'X';
        }
    }

    @Test
    void testValueOf() {
        final char[] chars = "pnbrqk".toCharArray();
        final List<Class<? extends Piece>> pieces = List.of(
            Pawn.class,
            Knight.class,
            Bishop.class,
            Rook.class,
            Queen.class,
            King.class
        );

        for (int i = 0; i < chars.length; i++) {

            final char currentChar = chars[i];
            final Class<? extends Piece> currentPieceClass = pieces.get(i);

            final Piece whitePiece = Piece.valueOf(
                    Character.toUpperCase(currentChar));
            final Piece blackPiece = Piece.valueOf(currentChar);

            assertInstanceOf(currentPieceClass, whitePiece);
            assertInstanceOf(currentPieceClass, blackPiece);

            assertEquals(Color.WHITE, whitePiece.getColor());
            assertEquals(Color.BLACK, blackPiece.getColor());
        }
    }

    @Test
    void testValueOfMalformed() {
        assertThrows(IllegalArgumentException.class, () -> Piece.valueOf('x'));
    }

    @Test
    void testGetLetter() {
        final Piece whitePiece = new TestPiece1(Color.WHITE);
        final Piece blackPiece = new TestPiece1(Color.BLACK);
    
        assertEquals('X', whitePiece.getLetter());
        assertEquals('x', blackPiece.getLetter());
    }

    @Test
    void testToString() {

        final Piece piece = new TestPiece1(Color.WHITE);
        final String expected = piece.getClass().getSimpleName();

        assertEquals(expected, piece.toString());
    }

    @Test
    void testEqualsTruePiece() {
        final Piece piece1 = new TestPiece1(Color.WHITE);
        final Piece piece2 = new TestPiece1(Color.WHITE);

        assertEquals(piece1, piece2);
    }
    @Test
    void testEqualsFalseDiffPiece() {
        final Piece piece1 = new TestPiece1(Color.WHITE);
        final Piece piece2 = new TestPiece2(Color.WHITE);

        assertNotEquals(piece1, piece2);
    }

    @Test
    void testEqualsFalseDiffColor() {
        final Piece piece1 = new TestPiece1(Color.WHITE);
        final Piece piece2 = new TestPiece1(Color.BLACK);

        assertNotEquals(piece1, piece2);
    }

    @Test
    void testEqualsFalseString() {
        final Piece piece = new TestPiece1(Color.WHITE);
        
        assertNotEquals("Hello, world!", piece);
    }

    @Test
    void testWhiteLetter() throws Exception {
        final Reflections reflections = new Reflections("gg.w6.chesslib.model.piece");
        Set<Class<? extends Piece>> pieceClasses = reflections.getSubTypesOf(Piece.class).stream()
                .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
                .collect(Collectors.toSet());

        assertFalse(pieceClasses.isEmpty(), "No Piece subclasses found");

        for (final Class<? extends Piece> cls : pieceClasses) {
            final Piece piece = cls.getConstructor(Color.class)
                    .newInstance(Color.WHITE);
            final char letter = cls.getDeclaredMethod("getWhiteLetter")
                    .invoke(piece).toString().charAt(0);
            
            assertTrue(Character.isUpperCase(letter), cls.getSimpleName()
                    + " returned lowercase or invalid letter");
        }
    }

}
