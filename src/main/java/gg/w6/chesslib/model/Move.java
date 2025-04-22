package gg.w6.chesslib.model;

import java.util.Objects;

import gg.w6.chesslib.model.piece.Piece;
import gg.w6.chesslib.util.Moves;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;

/**
 * <p>This class encapsulates the details of a move on a chessboard, including the origin and
 * destination squares, the type of move (such as normal, castling, en passant, or promotion),
 * and the piece to promote to (if applicable).</p>
 *
 * <p>This class is <b>immutable</b> and <b>thread-safe</b>. Once constructed, a {@code Move} instance
 * cannot be modified.</p>
 *
 * <p>To obtain a SAN (Standard Algebraic Notation) string for a move, use
 * {@link Moves#generateSAN(Move, Position)}.</p>
 */
@Immutable
public class Move {

    private final Coordinate from;
    private final Coordinate to;
    private final MoveType moveType;
    private final Piece promotionPiece;

    /**
     * Constructs a new {@code Move} object.
     *
     * @param from            the starting square of the move (must not be null)
     * @param to              the destination square of the move (must not be null). For castling,
     *                        this is the square the king moves to. For en passant, this is the
     *                        destination of the capturing pawn.
     * @param moveType        the type of the move (must not be null)
     * @param promotionPiece  the piece the pawn is promoted to, or null if not a promotion. This must only
     *                        be non-null if {@code moveType == MoveType.PROMOTION}.
     */
    public Move (@NotNull final Coordinate from, @NotNull final Coordinate to,
                 @NotNull final MoveType moveType,
                 @Nullable final Piece promotionPiece) {
        this.from = from;
        this.to = to;
        this.moveType = moveType;
        this.promotionPiece = promotionPiece;
    }

    /**
     * Returns the origin square of the move.
     *
     * @return the starting {@link Coordinate} of this move
     */
    @NotNull
    public Coordinate getFrom() {
        return from;
    }

    /**
     * Returns the destination square of the move.
     *
     * <p>For castling, this is the square the king moves to. For en passant, this is
     * the square the capturing pawn moves to.</p>
     *
     * @return the ending {@link Coordinate} of this move
     */
    @NotNull
    public Coordinate getTo() {
        return to;
    }

    /**
     * Returns the type of the move.
     *
     * @return the {@link MoveType} of this move
     */
    @NotNull
    public MoveType getMoveType() {
        return moveType;
    }

    /**
     * Returns the promotion piece, if this move is a promotion.
     *
     * @return the {@link Piece} the pawn is promoted to, or null if not a promotion
     */
    @Nullable
    public Piece getPromotionPiece() {
        return promotionPiece;
    }

    /**
     * Returns the move in UCI (Universal Chess Interface) format.
     *
     * <p>Examples: <code>e2e4</code>, <code>e7e8q</code>, <code>e1g1</code>. For castling, the king's
     * destination square is used (e.g., <code>e1g1</code> or <code>e8c8</code>). For en passant,
     * the destination square is the capturing pawn’s landing square.</p>
     *
     * @return a string representation of this move in UCI format
     */
    @Override
    @NotNull
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(from);
        stringBuilder.append(to);
        if (promotionPiece != null) {
            stringBuilder.append(Character.toLowerCase(promotionPiece.getLetter()));
        }
        return stringBuilder.toString();
    }

    /**
     * Compares this move to another object for equality.
     *
     * <p>Two moves are considered equal if their origin and destination squares, move type,
     * and promotion piece (if any) are all equal.</p>
     *
     * @param obj the object with which to compare to
     * @return <code>true</code> if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return
                obj instanceof final Move otherMove
                && from.equals(otherMove.from)
                && to.equals(otherMove.to)
                && moveType == otherMove.moveType
                && (Objects.equals(promotionPiece, otherMove.promotionPiece));
    }

    /**
     * Computes the hash code for this move.
     *
     * <p>The hash code is based on the origin square, destination square, move type, and
     * promotion piece (if any).</p>
     *
     * @return the hash code of this move
     */
    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        result = 31 * result + moveType.hashCode();
        result = 31 * result + (promotionPiece != null ? promotionPiece.hashCode() : 0);
        return result;
    }
}
