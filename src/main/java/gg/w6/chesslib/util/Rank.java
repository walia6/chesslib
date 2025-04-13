package gg.w6.chesslib.util;

/**
 * Represents the ranks on a chessboard.
 *
 * <p>Ranks are the horizontal rows on a chessboard, numbered from 1 to 8.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Rank rank = Rank.ONE;
 * }</pre>
 */
public enum Rank {
    /**
     * Represents the 1st rank.
     */
    ONE,

    /**
     * Represents the 2nd rank.
     */
    TWO,

    /**
     * Represents the 3rd rank.
     */
    THREE,

    /**
     * Represents the 4th rank.
     */
    FOUR,

    /**
     * Represents the 5th rank.
     */
    FIVE,

    /**
     * Represents the 6th rank.
     */
    SIX,

    /**
     * Represents the 7th rank.
     */
    SEVEN,

    /**
     * Represents the 8th rank.
     */
    EIGHT;

    /**
     * The number of ranks on a chessboard.
     */
    public static final int COUNT = 8;

    /**
     * Returns the character representation of the rank.
     *
     * @return The character representation of the rank.
     */
    @Override
    public String toString() {
        return String.valueOf(this.ordinal() + 1);
    }
}
