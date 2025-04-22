package gg.w6.chesslib.model.piece;

import gg.w6.chesslib.model.Color;
import gg.w6.chesslib.model.Offset;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

/**
 * Represents a King piece.
 *
 * <p>The King moves one square in any direction (horizontally, vertically, or diagonally).</p>
 *
 * <p>Notably, this class extends <code>Rider</code>.</p>
 * <p>See {@link Rider}.</p>
 */
@Immutable
public class King extends Rider {
    
    private static final char WHITE_LETTER = 'K';

    private static final Offset[] OFFSETS = {
            Offset.valueOf(0, 1), Offset.valueOf(1, 0),
            Offset.valueOf(1, 1), Offset.valueOf(0, -1),
            Offset.valueOf(1, -1), Offset.valueOf(-1, 0),
            Offset.valueOf(-1, 1), Offset.valueOf(-1, -1) };

    private static final int RANGE = 1;

    /**
     * Constructs a new King with the specified color.
     *
     * @param color The color of the King (either WHITE or BLACK).
     */
    public King(@NotNull final Color color) {
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
