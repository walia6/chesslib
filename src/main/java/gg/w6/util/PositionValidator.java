package gg.w6.util;

import java.util.HashSet;
import java.util.Set;

import gg.w6.core.Position;
import gg.w6.core.Square;
import gg.w6.piece.King;
import gg.w6.piece.Pawn;
import gg.w6.piece.Piece;
import gg.w6.piece.Rider;


/**
 * Provides utilities for validating the legality of a given chess {@link Position}.
 *
 * <p>This class offers static methods to perform rule-based checks on a position's state,
 * such as verifying the correct number of kings, detecting illegal pawn placements,
 * and identifying whether the side not to move is currently in check.</p>
 *
 * <p>The main entry point is {@link #getLegality(Position)}, which returns a {@link Legality}
 * object indicating whether the position is legal or illegal, along with an error message if invalid.</p>
 *
 * <p>Checks performed include:</p>
 * <ul>
 *   <li>Exactly one white king and one black king must be present.</li>
 *   <li>No pawns may be on the first or eighth ranks.</li>
 *   <li>The side not to move must not be in check.</li>
 *   <li>Pawns cannot attack the king not to move.</li>
 * </ul>
 *
 * <p>This class is non-instantiable and intended for static access only.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Position position = Position.valueOf("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
 * PositionValidator.Legality legality = PositionValidator.getLegality(position);
 * if (!legality.isLegal()) {
 *     System.out.println("Invalid position: " + legality.getErrorMessage());
 * }
 * }</pre>
 *
 * @see Position
 * @see Square
 * @see Piece
 */
public class PositionValidator {

    // Ensure non-instantiability
    private PositionValidator() {}

    /**
     * Represents the legality of a chess position.
     *
     * <p>This class encapsulates the result of a legality check, indicating whether
     * the position is legal or illegal, along with an optional error message.</p>
     *
     * <p>Use {@link #isLegal()} to check if the position is valid, and {@link #getErrorMessage()}
     * to retrieve any error message if the position is illegal.</p>
     */
    public static class Legality {
        private final boolean legal;
        private final String errorMessage;

        protected Legality(boolean legal) {
            this.legal = legal;
            this.errorMessage = null;
        }

        protected Legality(String errorMessage) {
            this.legal = false;
            this.errorMessage = errorMessage;
        }

        /**
         * Returns the error message if the position is illegal.
         * @return The error message.
         * @throws IllegalStateException if the position is legal.
         */
        public String getErrorMessage() {
            if (this.legal) throw new IllegalStateException
                    ("No error: the result is legal");
            return errorMessage;
        }

        /**
         * Checks if the position is legal.
         * @return True if the position is legal, false otherwise.
         */
        public boolean isLegal() {
            return legal;
        }
    }

    /**
     * Checks whether the given {@link Position} is legally valid according to static chess rules.
     *
     * <p>This method validates the structural and tactical legality of a board position without
     * considering move history or turn-by-turn legality. It performs checks such as:</p>
     *
     * <ul>
     *   <li>Exactly one white king and one black king must be present.</li>
     *   <li>No pawns may be placed on the first or eighth ranks.</li>
     *   <li>The king of the side <i>not</i> to move must not be in check.</li>
     *   <li>All pieces (including pawns and sliding pieces like bishops, rooks, queens)
     *       are checked to ensure they are not attacking the opponent's king.</li>
     * </ul>
     *
     * <p>Returns a {@link Legality} object that indicates whether the position is valid,
     * and includes an error message if not.</p>
     *
     * @param position the {@link Position} to validate
     * @return a {@link Legality} object representing the validity of the given position
     */
    public static Legality getLegality(Position position)
            throws IllegalStateException {
        
        Square whiteKingSquare = null;
        Square blackKingSquare = null;
        Set<Square> occupiedSquares = new HashSet<>();

        for (File file : File.values()) {

            for (Rank rank : Rank.values()) {

                Square currentSquare = position.getSquare(file, rank);
                Piece currentPiece = currentSquare.getPiece();

                if (currentPiece == null) continue;

                if (currentPiece instanceof King) {
                    if (currentPiece.getColor() == Color.WHITE) {
                        if (whiteKingSquare != null) {
                            return new Legality("Too many white kings for "
                                    + "position \"" + position.generateFEN()
                                    + "\".");
                        }
                        whiteKingSquare = currentSquare;
                    } else {
                        if (blackKingSquare != null) {
                            return new Legality("Too many black kings for "
                                    + "position \"" + position.generateFEN()
                                    + "\".");
                        }
                        blackKingSquare = currentSquare;
                    }
                }

                occupiedSquares.add(currentSquare);
            }

        }

        if (whiteKingSquare == null || blackKingSquare == null) {
            return new
                    Legality("Missing a white and/or black king for position \""
                    + position.generateFEN() + "\".");
        }

        final Color toMove = position.getToMove();

        final Square notToMoveKingSquare = toMove == Color.WHITE
                ? blackKingSquare
                : whiteKingSquare;

        for (final Square square : occupiedSquares) {
            if (square.equals(notToMoveKingSquare)) continue;

            final Piece piece = square.getPiece();
            final Color pieceColor = piece.getColor();

            if (pieceColor != position.getToMove() && !(piece instanceof Pawn))
                continue;

            if (piece instanceof Pawn) {

                if (square.getCoordinate().getRank() == Rank.ONE
                        || square.getCoordinate().getRank() == Rank.EIGHT)
                    return new Legality("A pawn was found on the first or "
                            + "eighth rank for position \""
                            + position.generateFEN() + "\".");

                if (pieceColor != toMove) continue;

                if (Math.abs(notToMoveKingSquare.getCoordinate().getFile()
                        .ordinal() - square.getCoordinate().getFile().ordinal())
                        == 1 && notToMoveKingSquare.getCoordinate().getRank()
                        .ordinal() - square.getCoordinate().getRank().ordinal()
                        == (toMove == Color.WHITE ? 1 : -1))
                    return new Legality("The pawn at " + square.getCoordinate()
                            + " is attacking the "
                            + notToMoveKingSquare.getPiece().getColor()
                            + " king at " + notToMoveKingSquare.getCoordinate()
                            + " for position \"" + position.generateFEN()
                            + "\".");
            }

            if (piece instanceof Rider) {
                Rider rider = (Rider) piece;
                for (Offset offset : rider.getOffsets()) {
                    for (Coordinate coordinate
                            : offset.extendFrom(
                            square.getCoordinate(), rider.getRange())) {
                        Square visionSquare = position.getSquare(coordinate);
                        Piece visionPiece = visionSquare.getPiece();
                        if (visionPiece == null) continue;
                        if (visionSquare.equals(notToMoveKingSquare)) {
                            return new Legality("The " + notToMoveKingSquare
                                    .getPiece().getColor() + " king on "
                                    + notToMoveKingSquare.getCoordinate()
                                    + " is in check by the " + rider.getColor()
                                    + " " + rider + " on " + square
                                    .getCoordinate() + " for position \""
                                    + position.generateFEN() + "\".");
                        } else break;
                    }
                }
            }
        }
        return new Legality(true);
    }
}
