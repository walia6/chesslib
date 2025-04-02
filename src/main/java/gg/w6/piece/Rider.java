package gg.w6.piece;

import gg.w6.util.Color;
import gg.w6.util.Offset;

/**
 * Abstract class representing a Rider piece in chess.
 *
 * <p>Rider pieces can move in multiple directions and have a specified range.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Rider rider = new Knight(Color.WHITE);
 * Offset[] offsets = rider.getOffsets(); // returns the movement offsets for the Rider
 * }</pre>
 */
public abstract class Rider extends Piece {
    protected Rider(final Color color) {
        super(color);
    }

    /**
     * Returns the offsets for the Rider's movement.
     *
     * @return An array of Offset objects representing the movement directions.
     */
    public abstract Offset[] getOffsets();

    /**
     * Returns the range of the Rider's movement.
     *
     * @return An integer representing the range of movement.
     */
    public abstract int getRange();

}
