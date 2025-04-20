package gg.w6.chesslib.model.piece;

import gg.w6.chesslib.model.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;

/**
 * Represents a chess piece.
 *
 * <p>This is an abstract class that serves as a base for all pieces.</p>
 */
@Immutable
public abstract class Piece {

    /**
     * The color of the piece.
     */
    private final Color color;

    /**
     * Constructs a new Piece with the specified color.
     *
     * @param color The color of the Piece (either WHITE or BLACK).
     */
    protected Piece(@NotNull final Color color) {
        this.color = color;
    }

    /**
     * Returns the color of the piece.
     *
     * @return The color of the piece (either WHITE or BLACK).
     */
    @NotNull
    public Color getColor() {
        return color;
    }

    /**
     * Returns the character representation of the white piece. This should always be uppercase.
     * @return The character representation of the white piece.
     */
    abstract char getWhiteLetter();

    /**
     * Returns the character representation of the piece.
     * <p>For white pieces, it returns the uppercase letter. For black pieces, it returns the lowercase letter.</p>
     *
     * @return The character representation of the piece.
     */
    public char getLetter() {
        final char whiteLetter = getWhiteLetter();
        return color == Color.WHITE
                ? whiteLetter
                : Character.toLowerCase(whiteLetter);
    }

    /**
     * Returns the piece corresponding to the given character.
     *
     * @param c The character representation of the piece (either uppercase for white or lowercase for black).
     * @return The corresponding Piece object.
     * @throws IllegalArgumentException if the character does not represent a valid piece.
     */
    @NotNull
    static public Piece valueOf(char c) {
        Color color = Character.toUpperCase(c) == c ? Color.WHITE : Color.BLACK;

        return switch (Character.toUpperCase(c)) {
            case 'P' -> new Pawn(color);
            case 'N' -> new Knight(color);
            case 'B' -> new Bishop(color);
            case 'R' -> new Rook(color);
            case 'Q' -> new Queen(color);
            case 'K' -> new King(color);
            default -> throw new IllegalArgumentException
                    ("Malformed piece char '" + c + "''.");
        };

    }

    /**
     * @return the simple name of the implementing class
     */
    @Override
    @NotNull
    public String toString() {
        return this.getClass().getSimpleName();
    }

    /**
     * Compares <code>this</code> and the current
     * @param obj the object with which to compare against
     * @return <code>true</code> if the object is of the same class and color.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        return
                obj instanceof final Piece piece
                && this.getClass().equals(obj.getClass())
                && piece.getLetter() == getLetter();
    }
}