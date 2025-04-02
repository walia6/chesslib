package gg.w6.piece;

import gg.w6.util.Color;
import gg.w6.util.Offset;

/**
 * Represents a Queen piece in chess.
 *
 * <p>The Queen can move any number of squares in any direction: horizontally, vertically, or diagonally.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Queen queen = new Queen(Color.WHITE);
 * Offset[] offsets = queen.getOffsets(); // returns the movement offsets for the Queen
 * }</pre>
 */
public class Queen extends Rider {

    private static final char WHITE_LETTER = 'Q';

    private static final Offset[] OFFSETS = {
            Offset.valueOf(0, 1), Offset.valueOf(1, 0),
            Offset.valueOf(1, 1), Offset.valueOf(0, -1),
            Offset.valueOf(1, -1), Offset.valueOf(-1, 0),
            Offset.valueOf(-1, 1), Offset.valueOf(-1, -1) };

    private static final int RANGE = Integer.MAX_VALUE;

    /**
     * Constructs a new Queen with the specified color.
     *
     * @param color The color of the Queen (either WHITE or BLACK).
     */
    public Queen(Color color) {
        super(color);
    }

    @Override
    public Offset[] getOffsets() {
        return OFFSETS;
    }

    @Override
    public int getRange() {
        return RANGE;
    }

    @Override
    char getWhiteLetter() {
        return WHITE_LETTER;
    }

}
