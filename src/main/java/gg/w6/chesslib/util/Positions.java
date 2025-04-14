package gg.w6.chesslib.util;

import gg.w6.chesslib.model.*;
import gg.w6.chesslib.model.piece.King;
import gg.w6.chesslib.model.piece.Pawn;
import gg.w6.chesslib.model.piece.Piece;
import gg.w6.chesslib.model.piece.Rider;

public class Positions {

    private Positions() {
    } // ensure non-instantiability

    public static boolean isKingToMoveInCheck(final Position position) {
        for (int fileIndex = 0; fileIndex < File.COUNT; fileIndex++) {
            for (int rankIndex = 0; rankIndex < Rank.COUNT; rankIndex++) {
    
                final Square origin = position.getSquare(fileIndex, rankIndex);
                final Piece originPiece = origin.getPiece();
    
                if (originPiece == null)
                    continue;
                if (originPiece.getColor() == position.getToMove())
                    continue;
    
                if (originPiece instanceof final Rider rider) {
                    for (final Offset offset : rider.getOffsets()) {
                        for (final Coordinate coordinate : offset.extendFrom(origin.getCoordinate(),
                                rider.getRange())) {
                            final Square targetSquare = position.getSquare(coordinate);
                            final Piece targetPiece = targetSquare.getPiece();
    
                            if (targetPiece == null)
                                continue;
    
                            if (targetPiece.getColor() == position.getToMove() && targetPiece instanceof King) {
                                return true;
                            } else {
                                break;
                            }
                        }
                    }
                } else if (originPiece instanceof Pawn) {
                    final int direction = originPiece.getColor() == Color.WHITE ? 1 : -1;
    
                    if (fileIndex != 0) {
                        if (!position.getSquare(fileIndex - 1, rankIndex + direction).isEmpty()
                                && position.getSquare(fileIndex - 1, rankIndex + direction).getPiece() instanceof King
                                && position.getSquare(fileIndex - 1, rankIndex + direction).getPiece()
                                        .getColor() == position.getToMove()) {
                            return true;
                        }
                    }
    
                    if (fileIndex != File.COUNT - 1) {
                        if (!position.getSquare(fileIndex + 1, rankIndex + direction).isEmpty()
                                && position.getSquare(fileIndex + 1, rankIndex + direction).getPiece() instanceof King
                                && position.getSquare(fileIndex + 1, rankIndex + direction).getPiece()
                                        .getColor() == position.getToMove()) {
                            return true;
                        }
                    }
    
                } else {
                    throw new IllegalStateException("The " + originPiece.getColor()
                            + "\"" + originPiece + "\" at " + origin + " is not a"
                            + " Rider or Pawn. FEN: \"" + position.generateFEN()
                            + "\".");
                }
            }
        }
        return false;
    }
    
}
