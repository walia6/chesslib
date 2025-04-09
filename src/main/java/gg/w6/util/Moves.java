package gg.w6.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gg.w6.core.Position;
import gg.w6.core.Square;
import gg.w6.piece.Bishop;
import gg.w6.piece.King;
import gg.w6.piece.Knight;
import gg.w6.piece.Pawn;
import gg.w6.piece.Piece;
import gg.w6.piece.Queen;
import gg.w6.piece.Rider;
import gg.w6.piece.Rook;

public final class Moves {

    public static boolean isCapture(final Move move, final Position position) {
        return position.getSquare(move.getTo()).getPiece() != null
                || move.getMoveType() == MoveType.EN_PASSANT;
    }

    public static boolean isPawnMove(final Move move, final Position position) {
        return position.getSquare(move.getFrom()).getPiece() instanceof Pawn;
    }

    public static Coordinate getEnPassantTarget(final Move move,
            final Position position) {
        final Piece movedPiece = position.getSquare(move.getFrom()).getPiece();

        if (!(movedPiece instanceof Pawn))
            return null;

        final Coordinate from = move.getFrom();

        final int fromRankIndex = from.getRank().ordinal();
        final int toRankIndex = move.getTo().getRank().ordinal();

        return Math.abs(fromRankIndex - toRankIndex) == 2
                ? Coordinate.valueOf(from.getFile().ordinal(),
                        (fromRankIndex + toRankIndex) / 2)
                : null;
    }

    public static boolean isPawnDoublePush(final Move move,
            final Position position) {
        final Piece movedPiece = position.getSquare(move.getFrom()).getPiece();
        final Coordinate from = move.getFrom();
        final Coordinate to = move.getTo();

        return movedPiece instanceof Pawn
                && from.getFile() == to.getFile()
                && Math.abs(from.getRank().ordinal()
                        - to.getRank().ordinal()) == 2;
    }

    public static Move generateMoveFromLAN(final Position position,
            final String lan) {
        Coordinate from; // done
        Coordinate to; // done
        Piece movedPiece; // done
        MoveType moveType; // done
        Piece promotionPiece;

        String fromSquare;
        boolean isCapture;
        String toSquare;
        String promotion;

        final String regex = "([NBRQK])?([a-h][1-8])([-x])([a-h][1-8])([NBRQK])?";
        final Matcher matcher = Pattern.compile(regex).matcher(lan);

        if (!matcher.matches())
            throw new IllegalArgumentException(
                    "Invalid LAN: " + lan);

        fromSquare = matcher.group(2);
        isCapture = matcher.group(3).equals("x");
        toSquare = matcher.group(4);
        promotion = matcher.group(5); // null if no promotion

        from = Coordinate.valueOf(fromSquare);
        to = Coordinate.valueOf(toSquare);
        movedPiece = position.getSquare(from).getPiece();

        if (movedPiece instanceof King &&
                (lan.equals("e1-g1")
                        || lan.equals("e1-c1")
                        || lan.equals("e8-g8")
                        || lan.equals("e8-c8"))) {
            moveType = MoveType.CASTLING;
        } else if (movedPiece instanceof Pawn && promotion != null) {
            moveType = MoveType.PROMOTION;
        } else if (movedPiece instanceof Pawn && isCapture
                && position.getSquare(to).getPiece() == null) {
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
                    throw new IllegalArgumentException(
                            "Invalid promotion piece: " + promotion);
            }
        } else {
            promotionPiece = null;
        }

        return new Move(from, to, moveType, promotionPiece);
    }

    public static boolean isKingToMoveInCheck(final Position position) {
        for (int fileIndex = 0; fileIndex < File.COUNT; fileIndex++) {
            for (int rankIndex = 0; rankIndex < Rank.COUNT; rankIndex++) {

                final Square origin = position.getSquare(fileIndex, rankIndex);
                final Piece originPiece = origin.getPiece();

                if (originPiece == null)
                    continue;
                if (originPiece.getColor() == position.getToMove())
                    continue;

                if (originPiece instanceof Rider) {
                    final Rider rider = (Rider) originPiece;
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

    public static String generateSAN(final Move move, final Position position) {

        final StringBuilder sanStringBuilder = new StringBuilder();

        if (move.getMoveType() == MoveType.CASTLING) {
            sanStringBuilder.append(
                switch (move.getTo().getFile()) {
                    case G -> "O-O";
                    case C -> "O-O-O";
                    default -> throw new IllegalArgumentException("Invalid castling destination " + move.getTo() + ".");
                }
            );
            
            appendSANSuffix(sanStringBuilder, move, position.applyTo(move));
            return sanStringBuilder.toString();
        }

        final Piece fromPiece = position.getSquare(move.getFrom()).getPiece();

        if (isPawnMove(move, position)) {
            if (isCapture(move, position)) {

                // <from file>
                sanStringBuilder.append(move.getFrom().getFile());

                // 'x'
                sanStringBuilder.append("x");

            }

            // <to square>
            sanStringBuilder.append(move.getTo());

            // [<promoted to>]
            if (move.getMoveType() == MoveType.PROMOTION) {
                if (move.getPromotionPiece() == null) {
                    throw new IllegalArgumentException("Can't promote to null piece");
                }
                sanStringBuilder.append("=");
                sanStringBuilder.append(Character.toUpperCase(move.getPromotionPiece().getLetter()));
            }

        } else {
            // <Piece symbol>
            sanStringBuilder.append(Character.toUpperCase(fromPiece.getLetter()));

            // [<from file>|<from rank>|<from square>]
            final boolean fileIsAmbiguous = Moves.isFileAmbiguous(move, position);
            final boolean rankIsAmbiguous = Moves.isRankAmbiguous(move, position);

            if (isPieceAmbiguous(move, position)) {
                if (fileIsAmbiguous && rankIsAmbiguous) {
                    sanStringBuilder.append(move.getFrom());
                } else if (!(fileIsAmbiguous && !rankIsAmbiguous)) {
                    sanStringBuilder.append(move.getFrom().getFile());
                } else {
                    sanStringBuilder.append(move.getFrom().getRank());
                } 
            }

            // ['x']
            if (Moves.isCapture(move, position)) {
                sanStringBuilder.append("x");
            }

            // <to square>
            sanStringBuilder.append(move.getTo());

        }

        appendSANSuffix(sanStringBuilder, move, position.applyTo(move));

        return sanStringBuilder.toString();
    }






    private static void appendSANSuffix(final StringBuilder sanStringBuilder, final Move move,
            final Position position) {
        if (!Moves.isKingToMoveInCheck(position)) return;
        sanStringBuilder.append(MoveGenerator.getLegalMoves(position).isEmpty() ? "#" : "+");
    }

    private static boolean isPieceAmbiguous(final Move move, final Position position) {
        final Coordinate from = move.getFrom();
        for (final Move candidateMove : MoveGenerator.getLegalMoves(position)) {
            final Coordinate candidateFrom = candidateMove.getFrom();

            // if the piece is the same, the move is diff, and the tooCordinate is the same

            // continue if the move is the same.
            if (from.getFile() == candidateFrom.getFile() && from.getRank() == candidateFrom.getRank()) {
                continue;
            }

            // continue if the Pieces are not of the same class
            if (!position.getSquare(from).getPiece().getClass()
                    .equals(position.getSquare(candidateFrom).getPiece().getClass())) {
                continue;
            }

            // continue if the toCoordinate is not the same
            if (move.getTo().getFile() != candidateMove.getTo().getFile()
                    || move.getTo().getRank() != candidateMove.getTo().getRank()) {
                continue;
            }

            return true;

        }

        return false;
    }

    private static boolean isRankAmbiguous(final Move move, final Position position) {
        for (final Move candidateMove : MoveGenerator.getLegalMoves(position)) {
            if (candidateMove.getFrom().getFile() == move.getFrom().getFile()) {
                continue;
            }
            if (candidateMove.getFrom().getRank() != move.getFrom().getRank()) {
                continue;
            }
            if (!candidateMove.getTo().equals(move.getTo())) {
                continue;
            }
            if (!position.getSquare(candidateMove.getFrom()).getPiece().getClass()
                    .equals(position.getSquare(move.getFrom()).getPiece().getClass())) {
                continue;
            }
            return true;
        }

        return false;
    }

    private static boolean isFileAmbiguous(final Move move, final Position position) {
        for (final Move candidateMove : MoveGenerator.getLegalMoves(position)) {
            if (candidateMove.getFrom().getRank() == move.getFrom().getRank()) {
                continue;
            }
            if (candidateMove.getFrom().getFile() != move.getFrom().getFile()) {
                continue;
            }
            if (!candidateMove.getTo().equals(move.getTo())) {
                continue;
            }
            if (!position.getSquare(candidateMove.getFrom()).getPiece().getClass()
                    .equals(position.getSquare(move.getFrom()).getPiece().getClass())) {
                continue;
            }
            return true;
        }

        return false;
    }

    private Moves() {
    } // ensure non-instantiability

}
