package gg.w6.util;

import gg.w6.core.Position;
import gg.w6.piece.Pawn;
import gg.w6.piece.Piece;

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
        if (!Positions.isKingToMoveInCheck(position)) return;
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

    public static long perft(final Position position, int depth) {
        if (depth == 0) return 1;
        long nodes = 0;
        for (Move move : MoveGenerator.getLegalMoves(position)) {
            final Position newPosition = position.applyTo(move);
            nodes += perft(newPosition, depth - 1);
        }
        return nodes;
    }

    private Moves() {
    } // ensure non-instantiability

}
