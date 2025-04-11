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
    public static enum Legality {
        LEGAL,
        MISSING_KING,
        TOO_MANY_KINGS,
        CAN_CAPTURE_KING,
        ILLEGAL_PAWN_RANK,
        ILLEGAL_CASTLING_RIGHTS,
        ILLEGAL_EN_PASSANT,
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
                            return Legality.TOO_MANY_KINGS;
                        }
                        whiteKingSquare = currentSquare;
                    } else {
                        if (blackKingSquare != null) {
                            return Legality.TOO_MANY_KINGS;
                        }
                        blackKingSquare = currentSquare;
                    }
                }

                occupiedSquares.add(currentSquare);
            }

        }

        if (whiteKingSquare == null || blackKingSquare == null) {
            return Legality.MISSING_KING;
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
                    return Legality.ILLEGAL_PAWN_RANK;

                if (pieceColor != toMove) continue;

                if (Math.abs(notToMoveKingSquare.getCoordinate().getFile()
                        .ordinal() - square.getCoordinate().getFile().ordinal())
                        == 1 && notToMoveKingSquare.getCoordinate().getRank()
                        .ordinal() - square.getCoordinate().getRank().ordinal()
                        == (toMove == Color.WHITE ? 1 : -1))
                    return Legality.CAN_CAPTURE_KING;
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
                            return Legality.CAN_CAPTURE_KING;
                        } else break;
                    }
                }
            }
        }
        return Legality.LEGAL;
    }
}
