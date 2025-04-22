package gg.w6.chesslib.model.piece;

import gg.w6.chesslib.model.Color;
import gg.w6.chesslib.model.Offset;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

/**
 * Abstract class representing a piece in chess that consistently moves along an offset.
 *
 * <p>Rider pieces can move in multiple directions and have a specified range.</p>
 * <p>A key abstraction of this library is that all pieces except for the
 * <code>Pawn</code> implement <code>Rider</code>.</p>
 * <p>For example, a <code>Knight</code> has a range of <code>1</code> and the offsets
 * <code>(1, 2)</code>, <code>(1, -2)</code>, <code>(-1, 2)</code>,
 * <code>(-1, -2)</code>, <code>(2, 1)</code>, <code>(2, -1)</code>,
 * <code>(-2, 1)</code>, <code>(-2, -1)</code>
 * (instantiated as <code>Offset</code>s). </p>
 */
@Immutable
public abstract class Rider extends Piece {

    /**
     * Constructs a new Rider with the specified color.
     *
     * @param color The color of the Rider (either WHITE or BLACK).
     */
    protected Rider(@NotNull final Color color) {
        super(color);
    }

    /**
     * Returns the offsets for the Rider's movement.
     *
     * @return An array of Offset objects representing the movement directions.
     */
    @NotNull
    public abstract Offset[] getOffsets();

    /**
     * Returns the range of the Rider's movement.
     *
     * @return An integer representing the range of movement.
     */
    public abstract int getRange();

}
