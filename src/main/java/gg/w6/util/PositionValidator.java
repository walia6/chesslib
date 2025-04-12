package gg.w6.util;

import java.util.ArrayList;
import java.util.List;

import gg.w6.core.Position;
import gg.w6.core.Square;
import gg.w6.piece.*;


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

    public enum Legality {
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
        List<Square> occupiedSquares = new ArrayList<>();

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

            if (piece instanceof final Rider rider) {
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

        // TODO: return the rest of the enums? Don't want to slow down getLegalMoves() though...

        return Legality.LEGAL;
    }

    public static boolean isTargetedByColor(final Coordinate target, final Color targeterColor, final Position position) {
        for (int fileIndex = 0; fileIndex < File.COUNT; fileIndex++) {
            for (int rankIndex = 0; rankIndex < Rank.COUNT; rankIndex++) {
                final Coordinate origin = Coordinate.valueOf(fileIndex, rankIndex);
                final Piece piece = position.getSquare(origin).getPiece();

                if (piece == null || piece.getColor() != targeterColor) {
                    continue;
                }

                if (piece instanceof final Rider rider) {
                    for (final Offset offset : rider.getOffsets()) {
                        for (final Coordinate candidate : offset.extendFrom(origin, rider.getRange())) {
                            if (candidate.equals(target)) {
                                return true;
                            } else if(position.getSquare(candidate).getPiece() != null) {
                                break;
                            }
                        }
                    }
                } else {
                    // piece must be a pawn
                    
                    final int direction = targeterColor == Color.WHITE ? 1 : -1;
                    if (origin.getRankIndex() + direction == target.getRankIndex() && Math.abs(origin.getFileIndex() - target.getFileIndex()) == 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
