package gg.w6.chesslib.piece;

import gg.w6.chesslib.util.Color;

/**
 * Represents a Pawn piece in chess.
 *
 * <p>The Pawn moves forward one square, but captures diagonally. On its first move, it can move two squares forward.</p>
 * <p>It can also promote to a different piece when it reaches the opposite end of the board.</p>
 * <p>Also, the Pawn has a special move called "en passant" which allows it to capture an opponent's pawn that has moved two squares forward from its starting position.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Pawn pawn = new Pawn(Color.WHITE);
 * Offset[] offsets = pawn.getOffsets(); // returns the movement offsets for the Pawn
 * }</pre>
 */
public class Pawn extends Piece {

    private static final char WHITE_LETTER = 'P';

    public Pawn(Color color) {
        super(color);
    }

    @Override
    char getWhiteLetter() {
        return WHITE_LETTER;
    }

}
