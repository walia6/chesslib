package gg.w6.util;

/**
 * Represents the color of a chess piece.
 *
 * <p>Chess pieces can be either white or black.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Color color = Color.WHITE;
 * }</pre>
 */
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
        switch (c) {
            case 'b': case 'B': return BLACK;
            case 'w': case 'W': return WHITE;
        }
        throw new IllegalArgumentException("Malformed color '" + c + "'.");
    }
}
