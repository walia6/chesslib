package gg.w6.chesslib.util;

import gg.w6.chesslib.model.*;
import gg.w6.chesslib.model.piece.King;
import gg.w6.chesslib.model.piece.Pawn;
import gg.w6.chesslib.model.piece.Piece;
import gg.w6.chesslib.model.piece.Rider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A utility class providing static methods for analyzing chess {@link Position} states.
 *
 * <p>This class contains common operations such as determining if the king to move is in check,
 * whether a square is targeted by a specific color, and generating the vision of rider pieces
 * based on their movement capabilities and obstructions.</p>
 *
 * <p>This class is not instantiable.</p>
 */
public class Positions {

    private Positions() {
    } // ensure non-instantiability

    /**
     * Determines whether the king of the player whose turn it is in the given {@link Position}
     * is currently in check.
     *
     * <p>This method is useful for legal move generation.</p>
     *
     * @param position the current position to evaluate
     * @return {@code true} if the king to move is in check, {@code false} otherwise
     */
    public static boolean isKingToMoveInCheck(@NotNull final Position position) {
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
                                && Objects.requireNonNull(position.getSquare(fileIndex - 1, rankIndex + direction).getPiece())
                                        .getColor() == position.getToMove()) {
                            return true;
                        }
                    }
    
                    if (fileIndex != File.COUNT - 1) {
                        if (!position.getSquare(fileIndex + 1, rankIndex + direction).isEmpty()
                                && position.getSquare(fileIndex + 1, rankIndex + direction).getPiece() instanceof King
                                && Objects.requireNonNull(position.getSquare(fileIndex + 1, rankIndex + direction).getPiece())
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

    /**
     * Returns a list of coordinates that the given {@link Rider} piece can
     * "see" from its origin, taking into account board boundaries and blocking
     * pieces.
     *
     * @param rider the {@link Rider} piece to evaluate
     * @param originCoordinate the starting coordinate of the rider
     * @param position the current board position
     * @return a list of coordinates representing the rider's line of sight
     */
    @NotNull
    public static List<Coordinate> getRiderVision(@NotNull final Rider rider,
                                                  @NotNull final Coordinate originCoordinate,
                                                  @NotNull final Position position) {
        final List<Coordinate> visionList = new ArrayList<>();
        for (final Offset offset : rider.getOffsets()) {
            for (final Coordinate candidate : offset.extendFrom(originCoordinate, rider.getRange())) {
                visionList.add(candidate);
                if (position.getSquare(candidate).getPiece() != null) {
                    break;
                }
            }
        }
        return visionList;
    }

    /**
     * Determines whether the specified {@code target} square is attacked by any piece
     * of the given {@code targeterColor} in the provided {@link Position}.
     *
     * @param target the square being checked for targeting
     * @param targeterColor the color of the attacking pieces
     * @param position the current board position
     * @return {@code true} if the square is targeted, {@code false} otherwise
     */
    public static boolean isTargetedByColor(@NotNull final Coordinate target,
                                            @NotNull final Color targeterColor,
                                            @NotNull final Position position) {

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

    /**
     * Return the En Passant target square {@link Coordinate} for a
     * {@link Position} that results from making the given {@link Move}
     * <i>after</i> on the given position. Naturally, this only is non-null
     * if the move is a double pawn push.
     *
     * <p>This method is generally useful for parsing a FEN string and
     * generating a position from it.</p>
     *
     * @param position the position <i>before</i> the move is made.
     * @param move the move that is being made.
     * @return the En Passant Square coordinate for the resulting position.
     */
    @Nullable
    public static Coordinate getEnPassantTarget(@NotNull final Position position, @NotNull final Move move) {
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
}
