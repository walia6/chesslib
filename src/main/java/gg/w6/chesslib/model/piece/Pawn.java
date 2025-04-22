package gg.w6.chesslib.model.piece;

import gg.w6.chesslib.model.Color;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

/**
 * Represents a Pawn piece in chess.
 *
 * <p>The Pawn moves forward one square, but captures diagonally. On its first
 * move, it can move two squares forward.</p>
 * <p>It can also promote to a different piece when it reaches the opposite
 * end of the board.</p>
 * <p>Also, the Pawn has a special move called "en passant" which allows it to
 * capture an opponent's pawn that has moved two squares forward from its
 * starting position.</p>
 */
@Immutable
public class Pawn extends Piece {

    private static final char WHITE_LETTER = 'P';

    /**
     * Constructs a new Pawn with the specified color.
     *
     * @param color The color of the Pawn (either WHITE or BLACK).
     */
    public Pawn(@NotNull final Color color) {
        super(color);
    }

    @Override
    char getWhiteLetter() {
        return WHITE_LETTER;
    }

}
