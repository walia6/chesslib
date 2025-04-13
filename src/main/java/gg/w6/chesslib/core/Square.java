package gg.w6.chesslib.core;

import gg.w6.chesslib.piece.Piece;
import gg.w6.chesslib.util.Color;
import gg.w6.chesslib.util.Coordinate;
import gg.w6.chesslib.util.File;
import gg.w6.chesslib.util.Rank;


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
 * <p>Note: Squares are immutable in their coordinates but mutable in their piece state. This design allows
 * for flexible manipulation of the board while preserving the location identity of each square.</p>
 *
 * <p>Examples:</p>
 * <pre>{@code
 * Square a1 = new Square(File.A, Rank.ONE);
 * Piece piece = a1.getPiece(); // may be null
 * Color squareColor = a1.getColor(); // returns Color.BLACK
 * }</pre>
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
    public Square(final Coordinate coordinate) {
        this.piece = null;
        this.coordinate = coordinate;
    }

    /**
     * Constructs a new Square with the specified file and rank, and no piece.
     *
     * @param file The file of the square.
     * @param rank The rank of the square.
     */
    public Square(final File file, final Rank rank) {
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
    public Square(final Coordinate coordinate, final Piece piece) {
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
    public static Square valueOf(final int fileIndex, final int squareIndex
            , final Piece piece) {
        return new Square(Coordinate.valueOf(fileIndex, squareIndex), piece);
    }

    /**
     * Returns the coordinate of this square.
     * @return The coordinate of the square.
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Returns the piece occupying this square, or null if the square is empty.
     * @return The piece on the square, or null if empty.
     */
    public Piece getPiece() {
        return piece;
    }

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
