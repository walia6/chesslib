package gg.w6.chesslib.model.piece;

import gg.w6.chesslib.model.Color;
import gg.w6.chesslib.model.Offset;
import gg.w6.chesslib.model.PromotionCandidate;

/**
 * Represents a Knight piece in chess.
 *
 * <p>The Knight moves in an "L" shape: two squares in one direction and then one square perpendicular,
 * or one square in one direction and then two squares perpendicular.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Knight knight = new Knight(Color.WHITE);
 * Offset[] offsets = knight.getOffsets(); // returns the movement offsets for the Knight
 * }</pre>
 */
@PromotionCandidate
public class Knight extends Rider {

    private static final char WHITE_LETTER = 'N';

    private static final Offset[] OFFSETS = {
            Offset.valueOf(1, 2), Offset.valueOf(1, -2),
            Offset.valueOf(-1, 2), Offset.valueOf(-1, -2),
            Offset.valueOf(2, 1), Offset.valueOf(2, -1),
            Offset.valueOf(-2, 1), Offset.valueOf(-2, -1) };

    private static final int RANGE = 1;

    /**
     * Constructs a new Knight with the specified color.
     *
     * @param color The color of the Knight (either WHITE or BLACK).
     */
    public Knight(final Color color) {
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
