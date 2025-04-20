package gg.w6.chesslib.model;

import javax.annotation.concurrent.Immutable;

/**
 * Represents the color of a chess piece.
 *
 * <p>Chess pieces can be either white or black.</p>
 */
@Immutable
public enum Color {

    /**
     * Represents the white color.
     */
    WHITE,

    /**
     * Represents the black color.
     */
    BLACK;

    /**
     * Returns the color corresponding to the given character.
     *
     * @param c The character representation of the color ('w' or 'b').
     * @return The corresponding Color object.
     * @throws IllegalArgumentException if the character does not represent a valid color.
     */
    public static Color valueOf(final char c) {
        return switch (c) {
            case 'b', 'B' -> BLACK;
            case 'w', 'W' -> WHITE;
            default -> throw new IllegalArgumentException("Malformed color '" + c + "'.");
        };
    }
}
