package gg.w6.chesslib.piece;

import gg.w6.chesslib.util.Color;

/**
 * Represents a chess piece.
 *
 * <p>This is an abstract class that serves as a base for all pieces.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Piece piece = new Pawn(Color.WHITE);
 * char letter = piece.getLetter(); // returns 'P'
 * }</pre>
 */
public abstract class Piece {

    /**
     * The color of the piece.
     */
    private final Color color;

    protected Piece(final Color color) {
        this.color = color;
    }

    /**
     * Returns the color of the piece.
     *
     * @return The color of the piece (either WHITE or BLACK).
     */
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


    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Piece && getClass().equals(obj.getClass()) && ((Piece) obj).getLetter() == getLetter();
    }
}