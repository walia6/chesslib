package gg.w6.chesslib.util;

import gg.w6.chesslib.model.*;
import gg.w6.chesslib.model.piece.Piece;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A mutable builder class for constructing {@link Position} instances programmatically.
 *
 * <p>This class allows fine-grained control over board setup, castling rights, en passant target,
 * active color, and move counters. It is especially useful for testing, FEN parsing, or generating
 * custom positions in code.</p>
 * 
 * <p>If a FEN string is already available, it may be more appropriate to use {@link Position#valueOf(String)} instead.</p>
 *
 * <p>Usage example:</p>
 * <pre><code>
 * Position position = new PositionBuilder()
 *     .addPiece(new Rook(Color.WHITE), Coordinate.valueOf("a1"))
 *     .addPiece(new King(Color.WHITE), Coordinate.valueOf("e1"))
 *     .setToMove(Color.BLACK)
 *     .setFullMoves(10)
 *     .toPosition();
 * </code></pre>
 *
 * <p>This builder is reusable and can be reset using {@link #reset()}.</p>
 */
public class PositionBuilder {

    private static final Square[][] EMPTY_BOARD;

    static {
        EMPTY_BOARD = new Square[File.COUNT][Rank.COUNT];
        for (int fileIndex = 0; fileIndex < File.COUNT; fileIndex++) {
            for (int rankIndex = 0; rankIndex < Rank.COUNT; rankIndex++) {
                EMPTY_BOARD[fileIndex][rankIndex] = new Square(fileIndex, rankIndex);
            }
        }
    }

    private final Square[][] squares;
    private boolean whiteKingsideRight;
    private boolean whiteQueensideRight;
    private boolean blackKingsideRight;
    private boolean blackQueensideRight;
    private Coordinate enPassantTarget;
    private Color toMove;
    private int halfMoveClock;
    private int fullMoves;

    private boolean filledEmptySquares;

    /**
     * Constructs a new {@code PositionBuilder} with default starting parameters.
     *
     * <p>Initially, all castling rights are granted, the turn is set to white, and the board is empty.
     * Empty squares are lazily initialized during {@link #toPosition()} unless filled earlier.</p>
     */
    public PositionBuilder() {
        this.squares = new Square[File.COUNT][Rank.COUNT];
        this.whiteKingsideRight = true;
        this.whiteQueensideRight = true;
        this.blackKingsideRight = true;
        this.blackQueensideRight = true;
        this.enPassantTarget = null;
        this.toMove = Color.WHITE;
        this.halfMoveClock = 0;
        this.fullMoves = 1;
        filledEmptySquares = false;
    }

    /**
     * Sets whether white has kingside castling rights.
     *
     * @param whiteKingsideRight {@code true} if allowed, {@code false} otherwise
     * @return this <code>PositionBuilder</code> instance
     */
    @NotNull
    public PositionBuilder setWhiteKingsideRight(final boolean whiteKingsideRight) {
        this.whiteKingsideRight = whiteKingsideRight;

        return this;
    }

    /**
     * Sets whether white has queenside castling rights.
     *
     * @param whiteQueensideRight {@code true} if allowed, {@code false} otherwise
     * @return this <code>PositionBuilder</code> instance
     */
    @NotNull
    public PositionBuilder setWhiteQueensideRight(final boolean whiteQueensideRight) {
        this.whiteQueensideRight = whiteQueensideRight;

        return this;
    }

    /**
     * Sets whether black has kingside castling rights.
     *
     * @param blackKingsideRight {@code true} if allowed, {@code false} otherwise
     * @return this <code>PositionBuilder</code> instance
     */
    @NotNull
    public PositionBuilder setBlackKingsideRight(final boolean blackKingsideRight) {
        this.blackKingsideRight = blackKingsideRight;

        return this;
    }

    /**
     * Sets whether black has queenside castling rights.
     *
     * @param blackQueensideRight {@code true} if allowed, {@code false} otherwise
     * @return this <code>PositionBuilder</code> instance
     */
    @NotNull
    public PositionBuilder setBlackQueensideRight(final boolean blackQueensideRight) {
        this.blackQueensideRight = blackQueensideRight;

        return this;
    }


    /**
     * Sets all castling rights to those defined within the supplied
     * {@link CastlingRights}.
     *
     * @param castlingRights the {@code CastlingRights} instance to get
     *                       castling rights from
     * @return this <code>PositionBuilder</code> instance
     */
    @NotNull
    public PositionBuilder setCastlingRights(@NotNull final CastlingRights castlingRights) {
        this.whiteKingsideRight = castlingRights.whiteKingside();
        this.whiteQueensideRight = castlingRights.whiteQueenside();
        this.blackKingsideRight = castlingRights.blackKingside();
        this.blackQueensideRight = castlingRights.blackQueenside();

        return this;
    }


    /**
     * Sets the en passant target square (if any).
     *
     * @param enPassantTarget the coordinate of the en passant target, or {@code null} if none
     * @return this builder instance
     */
    @NotNull
    public PositionBuilder setEnPassantTarget(@Nullable final Coordinate enPassantTarget) {
        this.enPassantTarget = enPassantTarget;

        return this;
    }

    /**
     * Sets which color is to move.
     *
     * @param toMove the color to move next
     * @return this builder instance
     */
    @NotNull
    public PositionBuilder setToMove(@NotNull final Color toMove) {
        this.toMove = toMove;

        return this;
    }

    /**
     * Sets the half-move clock (used for the fifty-move rule).
     *
     * @param halfMoveClock the number of half-moves since the last capture or pawn advance
     * @return this builder instance
     */
    @NotNull
    public PositionBuilder setHalfMoveClock(final int halfMoveClock) {
        this.halfMoveClock = halfMoveClock;

        return this;
    }

    /**
     * Sets the full-move number (starts at 1 and increments after black's move).
     *
     * @param fullMoves the full-move count
     * @return this builder instance
     */
    @NotNull
    public PositionBuilder setFullMoves(final int fullMoves) {
        this.fullMoves = fullMoves;

        return this;
    }

    /**
     * Finalizes and constructs an immutable {@link Position} object from the
     * current state of the builder.
     *
     * <p><i>Implementation Note: Empty squares are lazily filled on first call
     * if not already initialized.</i></p>
     *
     * <p><i>Implementation Note: The return object of this method may be
     * memoized in the future, so callers should not rely on identity equality
     * of the returned instance. </i></p>
     *
     * @return a new {@link Position} object representing the current board state
     */
    @NotNull
    public Position toPosition() {
        if (!filledEmptySquares) {
            for (int fileIndex = 0; fileIndex < File.COUNT; fileIndex++) {
                for (int rankIndex = 0; rankIndex < Rank.COUNT; rankIndex++) {
                    if (this.squares[fileIndex][rankIndex] == null) {
                        this.squares[fileIndex][rankIndex] = EMPTY_BOARD[fileIndex][rankIndex];
                    }
                }
            }
            filledEmptySquares = true;
        }

        return new Position(
                copy(this.squares),
                new CastlingRights(
                        whiteKingsideRight,
                        whiteQueensideRight,
                        blackKingsideRight,
                        blackQueensideRight),
                enPassantTarget,
                toMove,
                halfMoveClock,
                fullMoves);
    }

    /**
     * Adds a piece to the specified coordinate on the board.
     *
     * <p>The <code>piece</code> parameter is <i>not</i> nullable. Callers
     * wishing to remove a piece should use {@link #removePiece(Coordinate)}</p>
     * @param piece the piece to place
     * @param coordinate the coordinate to place the piece on
     * @return this builder instance
     */
    @NotNull
    public PositionBuilder addPiece(@NotNull final Piece piece, @NotNull final Coordinate coordinate) {
        final int fileIndex = coordinate.getFileIndex();
        final int rankIndex = coordinate.getRankIndex();

        squares[fileIndex][rankIndex] = Square.valueOf(fileIndex, rankIndex, piece);

        return this;
    }

    /**
     * Removes a piece from the specified coordinate on the board.
     *
     * @param coordinate the coordinate to remove the piece from
     * @return this builder instance
     */
    @NotNull
    public PositionBuilder removePiece(@NotNull final Coordinate coordinate) {
        final int fileIndex = coordinate.getFileIndex();
        final int rankIndex = coordinate.getRankIndex();
        if (filledEmptySquares) {
            this.squares[fileIndex][rankIndex] = EMPTY_BOARD[fileIndex][rankIndex];
        } else {
            this.squares[fileIndex][rankIndex] = null;
        }

        return this;
    }

    /**
     * Resets the builder to its default state.
     *
     * <p>This clears the board, resets all castling rights, move counters, and en passant targets.</p>
     *
     * @return this builder instance
     */
    public PositionBuilder reset() {
        this.whiteKingsideRight = true;
        this.whiteQueensideRight = true;
        this.blackKingsideRight = true;
        this.blackQueensideRight = true;
        this.enPassantTarget = null;
        this.toMove = Color.WHITE;
        this.halfMoveClock = 0;
        this.fullMoves = 1;

        if (filledEmptySquares) {
            for (int fileIndex = 0; fileIndex < File.COUNT; fileIndex++) {
                for (int rankIndex = 0; rankIndex < Rank.COUNT; rankIndex++) {
                    if (squares[fileIndex][rankIndex] != EMPTY_BOARD[fileIndex][rankIndex]) {
                        squares[fileIndex][rankIndex] = EMPTY_BOARD[fileIndex][rankIndex];
                    }
                }
            }
        } else {
            for (int fileIndex = 0; fileIndex < File.COUNT; fileIndex++) {
                for (int rankIndex = 0; rankIndex < Rank.COUNT; rankIndex++) {
                    if (squares[fileIndex][rankIndex] != EMPTY_BOARD[fileIndex][rankIndex]) {
                        squares[fileIndex][rankIndex] = null;
                    }
                }
            }
        }

        return this;
    }

    private Square[][] copy(final Square[][] squares) {
        final Square[][] newSquares = new Square[File.COUNT][Rank.COUNT];
        for (int fileIndex = 0; fileIndex < File.COUNT; fileIndex++) {
            System.arraycopy(squares[fileIndex], 0, newSquares[fileIndex], 0, Rank.COUNT);
        }
        return newSquares;
    }
}
