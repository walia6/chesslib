package gg.w6.chesslib.util;


import gg.w6.chesslib.model.Move;
import gg.w6.chesslib.model.Position;

public class SanParser {

    public static Move parse(final String san, final Position position) {
        final String cleanSan = san.replace("#", "").replace("+", "");
        for (final Move move : MoveGenerator.getLegalMoves(position)) {
            if (Moves.generateSAN(move, position).replace("#", "").replace("+","").equals(cleanSan)) {
                return move;
            }
        }
        throw new IllegalArgumentException("Illegal SAN \"" + san + "\" for position \"" + position + "\".");
    }

    public static Move parse(final String san, final String fen) {
        return parse(san, Position.valueOf(fen));
    }

}
