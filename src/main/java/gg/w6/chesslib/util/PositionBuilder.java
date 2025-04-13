package gg.w6.chesslib.util;

import gg.w6.chesslib.core.Position;
import gg.w6.chesslib.core.Square;
import gg.w6.chesslib.piece.Piece;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public PositionBuilder setWhiteKingsideRight(final boolean whiteKingsideRight) {
        this.whiteKingsideRight = whiteKingsideRight;
        return this;
    }

    public PositionBuilder setWhiteQueensideRight(final boolean whiteQueensideRight) {
        this.whiteQueensideRight = whiteQueensideRight;
        return this;
    }

    public PositionBuilder setBlackKingsideRight(final boolean blackKingsideRight) {
        this.blackKingsideRight = blackKingsideRight;
        return this;
    }

    public PositionBuilder setBlackQueensideRight(final boolean blackQueensideRight) {
        this.blackQueensideRight = blackQueensideRight;
        return this;
    }

    public PositionBuilder setEnPassantTarget(@Nullable final Coordinate enPassantTarget) {
        this.enPassantTarget = enPassantTarget;
        return this;
    }

    public PositionBuilder setToMove(@NotNull final Color toMove) {
        this.toMove = toMove;
        return this;
    }

    public PositionBuilder setHalfMoveClock(final int halfMoveClock) {
        this.halfMoveClock = halfMoveClock;
        return this;
    }

    public PositionBuilder setFullMoves(final int fullMoves) {
        this.fullMoves = fullMoves;
        return this;
    }

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

    public PositionBuilder addPiece(@NotNull final Piece piece, @NotNull final Coordinate coordinate) {
        final int fileIndex = coordinate.getFileIndex();
        final int rankIndex = coordinate.getRankIndex();

        squares[fileIndex][rankIndex] = Square.valueOf(fileIndex, rankIndex, piece);

        return this;
    }

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
