package gg.w6.chesslib.model.piece;

import gg.w6.chesslib.model.Color;
import gg.w6.chesslib.model.Offset;
import gg.w6.chesslib.model.PromotionCandidate;

/**
 * Represents a Rook piece in chess.
 *
 * <p>The Rook moves horizontally or vertically any number of squares.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Rook rook = new Rook(Color.WHITE);
 * Offset[] offsets = rook.getOffsets(); // returns the movement offsets for the Rook
 * }</pre>
 */
@PromotionCandidate
public class Rook extends Rider {

    private static final char WHITE_LETTER = 'R';

    private static final Offset[] OFFSETS = {
            Offset.valueOf(1, 0), Offset.valueOf(-1, 0),
            Offset.valueOf(0, 1), Offset.valueOf(0, -1) };
    
    private static final int RANGE = Integer.MAX_VALUE;

    public Rook(Color color) {
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
