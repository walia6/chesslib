package gg.w6.chesslib.util;

import gg.w6.chesslib.model.Coordinate;
import gg.w6.chesslib.model.MoveType;
import gg.w6.chesslib.model.Position;
import gg.w6.chesslib.model.Move;
import gg.w6.chesslib.model.piece.Pawn;
import gg.w6.chesslib.model.piece.Piece;
import org.jetbrains.annotations.NotNull;

/**
 * This class consists exclusively of static methods that operate on or return
 * {@link Move}s. It is not instantiable.
 */
public final class Moves {

    /**
     * Return if the given move is a capture on a given position.
     *
     * @param move the {@link Move} to test
     * @param position the {@link Position} to test the move on
     * @return <code>true</code> if the given move is a capture on a given position
     */
    public static boolean isCapture(@NotNull final Move move, @NotNull final Position position) {
        return position.getSquare(move.getTo()).getPiece() != null
                || move.getMoveType() == MoveType.EN_PASSANT;
    }

    /**
     * Return if the given move is a {@link Pawn} move on a given position.
     *
     * @param move the {@link Move} to test
     * @param position the {@link Position} to test the move on
     * @return <code>true</code> if the given move is a {@link Pawn} move on a given position
     */
    public static boolean isPawnMove(@NotNull final Move move,
                                     @NotNull final Position position) {
        return position.getSquare(move.getFrom()).getPiece() instanceof Pawn;
    }

    /**
     * Determines if the given {@link Move} is a pawn double-push in the context
     * of a given {@link Position} <i>before</i> the move is made.
     *
     * @param move the move that is being made.
     * @param position the position <i>before</i> the move is made
     * @return <code>true</code> if the move is a pawn double-push
     */
    public static boolean isPawnDoublePush(@NotNull final Move move,
                                           @NotNull final Position position) {
        final Piece movedPiece = position.getSquare(move.getFrom()).getPiece();
        final Coordinate from = move.getFrom();
        final Coordinate to = move.getTo();

        return movedPiece instanceof Pawn
                && from.getFile() == to.getFile()
                && Math.abs(from.getRank().ordinal() - to.getRank().ordinal()) == 2;
    }

    /**
     * Generates the Standard Algebraic Notation (SAN) representation for the
     * given {@link Move} in the context of the provided {@link Position}
     * <i>before</i> the move is made.
     *
     * <p>This method accounts for special cases such as castling, captures, promotions,
     * ambiguous disambiguation, and check or checkmate markers.</p>
     *
     * @param move the {@link Move} to generate SAN for
     * @param position the {@link Position} the move is played in (before the
     *                 move is made)
     * @return the SAN string representation of the move
     * @throws IllegalArgumentException if the move is castling to an invalid
     *                                  file, or if there is no piece at the
     *                                  source square, or if the move is a
     *                                  promotion without a target piece
     */
    @NotNull
    public static String generateSAN(@NotNull final Move move, @NotNull final Position position) {

        final StringBuilder sanStringBuilder = new StringBuilder();

        if (move.getMoveType() == MoveType.CASTLING) {
            sanStringBuilder.append(
                switch (move.getTo().getFile()) {
                    case G -> "O-O";
                    case C -> "O-O-O";
                    default -> throw new IllegalArgumentException("Invalid castling destination " + move.getTo() + ".");
                }
            );

            appendSANSuffix(sanStringBuilder, position.applyTo(move));
            return sanStringBuilder.toString();
        }

        final Piece fromPiece = position.getSquare(move.getFrom()).getPiece();
        if (fromPiece == null) {
            throw new IllegalArgumentException("There is no piece at " + move.getFrom() + ".");
        }

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
                } else if (!fileIsAmbiguous) {
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

        appendSANSuffix(sanStringBuilder, position.applyTo(move));

        return sanStringBuilder.toString();
    }

    private static void appendSANSuffix(final StringBuilder sanStringBuilder,
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

    private Moves() {
    } // ensure non-instantiability

}
