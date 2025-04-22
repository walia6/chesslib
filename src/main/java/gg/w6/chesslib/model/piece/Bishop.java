package gg.w6.chesslib.model.piece;

import gg.w6.chesslib.model.Color;
import gg.w6.chesslib.model.Offset;
import gg.w6.chesslib.model.PromotionCandidate;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

/**
 * Represents a Bishop piece.
 *
 * <p>The Bishop moves diagonally across the board, and can move any number of squares in that direction.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Bishop bishop = new Bishop(Color.WHITE);
 * Offset[] offsets = bishop.getOffsets(); // returns the diagonal movement offsets
 * }</pre>
 */
@Immutable
@PromotionCandidate
public class Bishop extends Rider {

    private static final char WHITE_LETTER = 'B';

    /**
     * The offsets for the Bishop's movement.
     * The Bishop can move diagonally in all four directions.
     */
    private static final Offset[] OFFSETS = { 
            Offset.valueOf(-1, -1), Offset.valueOf(-1, 1),
            Offset.valueOf(1, -1), Offset.valueOf(1, 1) };
    
    /**
     * The range of the Bishop's movement.
     */
    private static final int RANGE = Integer.MAX_VALUE;

    /**
     * Constructs a new Bishop with the specified color.
     *
     * @param color The color of the Bishop (either WHITE or BLACK).
     */
    public Bishop(@NotNull final Color color) {
        super(color);
    }

    @Override
    @NotNull
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
