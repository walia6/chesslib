package gg.w6.util;

import java.util.regex.Matcher;

import gg.w6.core.Position;
import gg.w6.piece.Bishop;
import gg.w6.piece.King;
import gg.w6.piece.Knight;
import gg.w6.piece.Pawn;
import gg.w6.piece.Piece;
import gg.w6.piece.Queen;
import gg.w6.piece.Rook;

public class Moves {
    public static Move generateMoveFromString(final Position position, final String lan) {
        Coordinate from; // done
        Coordinate to; // done
        Piece movedPiece; // done
        Piece capturedPiece; //done
        MoveType moveType; // done
        Piece promotionPiece;





        String fromSquare;
        boolean isCapture;
        String toSquare;
        String promotion; 





        final String regex = "([NBRQK])?([a-h][1-8])([-x])([a-h][1-8])([NBRQK])?";
        final Matcher matcher = java.util.regex.Pattern.compile(regex).matcher(lan);
    
        if (!matcher.matches()) throw new IllegalArgumentException("Invalid LAN: " + lan);
    
        fromSquare = matcher.group(2);
        isCapture = matcher.group(3).equals("x");
        toSquare = matcher.group(4);
        promotion = matcher.group(5); // null if no promotion

        /*
        if (lan.equals("e1-g1")) {
            toSquare = "h1";
        } else if (lan.equals("e1-c1")) {
            toSquare = "a1";
        } else if (lan.equals("e8-g8")) {
            toSquare = "h8";
        } else if (lan.equals("e8-c8")) {
            toSquare = "a8";
        }*/

        

        from = Coordinate.valueOf(fromSquare);
        to = Coordinate.valueOf(toSquare);
        movedPiece = position.getSquare(from).getPiece();
        if (isCapture) {
            if (movedPiece instanceof Pawn && position.getSquare(to).getPiece() == null) {
                // En passant
                final int direction = movedPiece.getColor() == Color.WHITE ? -1 : 1;
                final int fileIdx = to.getFile().ordinal();
                final int rankIdx = to.getRank().ordinal() + direction;
                final Coordinate capturedCoord = Coordinate.valueOf(File.values()[fileIdx], Rank.values()[rankIdx]);
                capturedPiece = position.getSquare(capturedCoord).getPiece();
            } else {
                // Normal capture
                capturedPiece = position.getSquare(to).getPiece();
            }
        } else {
            capturedPiece = null;
        }

        if (movedPiece instanceof King && (lan.equals("e1-g1") || lan.equals("e1-c1") || lan.equals("e8-g8") || lan.equals("e8-c8"))) {
            moveType = MoveType.CASTLING;
        } else if (movedPiece instanceof Pawn && promotion != null) {
            moveType = MoveType.PROMOTION;
        } else if (movedPiece instanceof Pawn && isCapture && position.getSquare(to).getPiece() == null) {
            moveType = MoveType.EN_PASSANT;
        } else {
            moveType = MoveType.NORMAL;
        }

        if (promotion != null) {
            switch (promotion) {
                case "Q":
                    promotionPiece = new Queen(movedPiece.getColor());
                    break;
                case "R":
                    promotionPiece = new Rook(movedPiece.getColor());
                    break;
                case "B":
                    promotionPiece = new Bishop(movedPiece.getColor());
                    break;
                case "N":
                    promotionPiece = new Knight(movedPiece.getColor());
                    break;
                default:
                    throw new IllegalArgumentException("Invalid promotion piece: " + promotion);
            }
        } else {
            promotionPiece = null;
        }

        






        
        return new Move(from, to, movedPiece, capturedPiece, moveType, promotionPiece);
    }
    
}
