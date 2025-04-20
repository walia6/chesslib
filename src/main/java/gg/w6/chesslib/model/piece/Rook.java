package gg.w6.chesslib.model.piece;

import gg.w6.chesslib.model.Color;
import gg.w6.chesslib.model.Offset;
import gg.w6.chesslib.model.PromotionCandidate;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

/**
 * Represents a Rook piece in chess.
 *
 * <p>The Rook moves horizontally or vertically any number of squares.</p>
 */
@Immutable
@PromotionCandidate
public class Rook extends Rider {

    private static final char WHITE_LETTER = 'R';

    private static final Offset[] OFFSETS = {
            Offset.valueOf(1, 0), Offset.valueOf(-1, 0),
            Offset.valueOf(0, 1), Offset.valueOf(0, -1) };
    
    private static final int RANGE = Integer.MAX_VALUE;

    /**
     * Constructs a new Rook with the specified color.
     *
     * @param color The color of the Rook (either WHITE or BLACK).
     */
    public Rook(@NotNull final Color color) {
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
