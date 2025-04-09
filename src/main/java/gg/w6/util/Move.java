package gg.w6.util;

import gg.w6.piece.Piece;

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
        return "From: " + from + " To: " + to;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Move other = (Move) obj;
        return from.equals(other.from)
            && to.equals(other.to)
            && moveType == other.moveType
            && (promotionPiece == null ? other.promotionPiece == null : promotionPiece.equals(other.promotionPiece));
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
