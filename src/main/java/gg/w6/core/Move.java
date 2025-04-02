package gg.w6.core;

import gg.w6.piece.Pawn;
import gg.w6.piece.Piece;
import gg.w6.util.Coordinate;
import gg.w6.util.MoveType;

public class Move {

    private final Coordinate from;
    private final Coordinate to;
    private final Piece movedPiece;
    private final Piece capturedPiece;
    private final MoveType moveType;
    private final Piece promotionPiece;

    public Move (final Coordinate from, final Coordinate to,
            final Piece movedPiece, final Piece capturedPiece,
            final MoveType moveType, final Piece promotionPiece) {
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.moveType = moveType;
        this.promotionPiece = promotionPiece;
    }

    public Coordinate getFrom() {
        return from;
    }

    public Coordinate getTo() {
        return to;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public Piece getPromotionPiece() {
        return promotionPiece;
    }

    public boolean isCapture() {
        return this.capturedPiece != null;
    }

    public boolean isPawnMove() {
        return this.movedPiece instanceof Pawn;
    }

    public boolean isPawnDoublePush() {
        return 
            this.movedPiece instanceof Pawn
            && this.from.getFile() == this.to.getFile()
            && Math.abs(this.from.getRank().ordinal()
                    - this.to.getRank().ordinal()) == 2;
    }

    public Coordinate getEnPassantTarget() {
        if (!(movedPiece instanceof Pawn)) return null;
    
        int fromRankIndex = from.getRank().ordinal();
        int toRankIndex = to.getRank().ordinal();
    
        if (Math.abs(fromRankIndex - toRankIndex) != 2) return null;
    
        return Coordinate.valueOf(from.getFile().ordinal(), (fromRankIndex + toRankIndex) / 2);
    }
    
    
}
