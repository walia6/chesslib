package gg.w6.chesslib.model;

import gg.w6.chesslib.model.piece.Piece;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Represents a single square on a standard 8x8 chessboard.
 *
 * <p>Each square has a fixed {@link Coordinate} (file and rank), and may contain a {@link Piece}
 * or be empty (null). The square also provides utility methods to determine its color (black or white)
 * based on board position.</p>
 *
 * <p>Square objects are typically used as part of a 2D board array representing a full chess position,
 * and are responsible for maintaining the local state of piece placement.</p>
 *
 * @see Piece
 * @see Coordinate
 */

public class Square {
    /**
     * The piece occupying this square, or null if the square is empty.
     */
    private final Piece piece;

    /**
     * The coordinate of this square on the chessboard.
     */
    private final Coordinate coordinate;

    /**
     * Constructs a new Square with the specified coordinate and no piece.
     *
     * @param coordinate The coordinate of the square.
     */
    public Square(@NotNull final  Coordinate coordinate) {
        this.piece = null;
        this.coordinate = coordinate;
    }

    /**
     * Constructs a new Square with the specified file and rank, and no piece.
     *
     * @param file The file of the square.
     * @param rank The rank of the square.
     */
    public Square(@NotNull final File file, @NotNull final Rank rank) {
        this.piece = null;
        this.coordinate = Coordinate.valueOf(file, rank);
    }

    /**
     * Constructs a new Square with the specified file and rank, and no piece.
     *
     * @param fileIndex The index of the file.
     * @param rankIndex The index of the rank.
     */
    public Square(final int fileIndex, final int rankIndex) {
        this.piece = null;
        this.coordinate = Coordinate.valueOf(fileIndex, rankIndex);
    }

    /**
     * Constructs a new Square with the specified coordinate and piece.
     *
     * @param coordinate The coordinate of the square.
     * @param piece      The piece occupying the square, or null if empty.
     */
    public Square(@NotNull final Coordinate coordinate, @Nullable final Piece piece) {
        this.piece = piece;
        this.coordinate = coordinate;
    }

    /**
     * Returns a Square of the given properties.
     * @param fileIndex the file index
     * @param squareIndex the rank index
     * @param piece the piece (may be null)
     * @return a Square of the given properties.
     */
    @NotNull
    public static Square valueOf(final int fileIndex, final int squareIndex
            , @Nullable final Piece piece) {
        return new Square(Coordinate.valueOf(fileIndex, squareIndex), piece);
    }

    /**
     * Returns the coordinate of this square.
     * @return The coordinate of the square.
     */
    @NotNull
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Returns the piece occupying this square, or null if the square is empty.
     * @return The piece on the square, or null if empty.
     */
    @Nullable
    public Piece getPiece() {
        return piece;
    }

    /**
     * Checks if this square has a piece.
     * @return <code>true</code> if this square has a piece
     */
    public boolean isEmpty() {
        return this.piece == null;
    }

    /**
     * Returns the color of this square based on its coordinate.
     * @return The color of the square (black or white).
     */
    public Color getColor() {
        return (coordinate.getFile().ordinal() + coordinate.getRank().ordinal())
                % 2 == 0 ? Color.BLACK : Color.WHITE;
    }
}
