package gg.w6.chesslib.util;

import java.util.Objects;

import gg.w6.chesslib.piece.Piece;

public class Move {

    private final Coordinate from;
    private final Coordinate to;
    private final MoveType moveType;
    private final Piece promotionPiece;

    Move (final Coordinate from, final Coordinate to,
            final MoveType moveType, final Piece promotionPiece) {
        this.from = from;
        this.to = to;
        this.moveType = moveType;
        this.promotionPiece = promotionPiece;
    }

    public Coordinate getFrom() {
        return from;
    }

    public Coordinate getTo() {
        return to;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public Piece getPromotionPiece() {
        return promotionPiece;
    }
    
    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(from);
        stringBuilder.append(to);
        if (moveType == MoveType.PROMOTION) {
            stringBuilder.append(Character.toLowerCase(promotionPiece.getLetter()));
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Move other = (Move) obj;
        return from.equals(other.from)
            && to.equals(other.to)
            && moveType == other.moveType
            && (Objects.equals(promotionPiece, other.promotionPiece));
    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        result = 31 * result + moveType.hashCode();
        result = 31 * result + (promotionPiece != null ? promotionPiece.hashCode() : 0);
        return result;
}

}
