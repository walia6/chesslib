package gg.w6.chesslib.model;

import javax.annotation.concurrent.Immutable;
import java.util.Arrays;

/**
 * Represents a coordinate on a chessboard.
 *
 * <p>Coordinates are represented in the format "a1", "h8", etc.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Coordinate coordinate = Coordinate.valueOf("e4");
 * }</pre>
 */
@Immutable
public class Coordinate {
    
    /**
     * A cache of all possible coordinates on the chessboard.
     */
    private static final Coordinate[] coordinates = new Coordinate[Rank.COUNT * File.COUNT];

    /**
     * Creates a new Coordinate object with the specified file and rank.
     * @param file The file of the coordinate.
     * @param rank The rank of the coordinate.
     * @return A Coordinate object representing the specified file and rank.
     */
    public static Coordinate valueOf(final File file, final Rank rank) {
        return valueOf(file.ordinal(), rank.ordinal());
    }

    /**
     * Creates a new Coordinate object with the specified file and rank.
     * @param fileIndex The index of the file.
     * @param rankIndex The index of the rank.
     * @return A Coordinate object representing the specified file and rank.
     */
    public static Coordinate valueOf(final int fileIndex, final int rankIndex) {
        if (fileIndex > File.COUNT - 1
                || rankIndex > Rank.COUNT - 1
                || fileIndex < 0 || rankIndex < 0) {
            throw new IllegalArgumentException("Coordinate(" + fileIndex + ", "
                    + rankIndex + ") is out of bounds.");
        }
        return coordinates[rankIndex * Rank.COUNT + fileIndex] != null
                ? coordinates[rankIndex * Rank.COUNT + fileIndex]
                : (coordinates[rankIndex * Rank.COUNT + fileIndex] =
                        new Coordinate(fileIndex, rankIndex));
    }

    /**
     * Creates a new Coordinate object from a string representation.
     * @param coordinate The string representation of the coordinate (e.g., "e4").
     * @return A Coordinate object representing the specified string.
     * @throws IllegalArgumentException if the string is not a valid coordinate.
     */
    public static Coordinate valueOf(final String coordinate) {
        if (!coordinate.matches("^[a-h][1-8]$")) {
            throw new IllegalArgumentException(
                    "Malformed coordinate \"" + coordinate + "\".");
        }

        return Coordinate.valueOf(File.valueOf(String.valueOf(
                Character.toUpperCase(coordinate.charAt(0)))),
                Rank.values()[coordinate.charAt(1) - '1']);
    }

    /**
     * Resets the cache of coordinates.
     * <p>This method is used for testing purposes only.</p>
     */
    public static void resetCache() {
        Arrays.fill(coordinates, null);
    }

    /**
     * The file of the coordinate.
     */
    private final File file;

    /**
     * The rank of the coordinate.
     */
    private final Rank rank;

    private Coordinate(final int fileIndex, final int rankIndex) {
        this.file = File.values()[fileIndex];
        this.rank = Rank.values()[rankIndex];
    }

    /**
     * Returns the file of the coordinate.
     * @return The file of the coordinate.
     */
    public File getFile() {
        return file;
    }

    /**
     * Returns the rank of the coordinate.
     * @return The rank of the coordinate.
     */
    public Rank getRank() {
        return rank;
    }

    /**
     * Returns the index of the file of the coordinate
     * <p>Semantically equivalent to <code>getFile().ordinal()</code></p>
     * @return the index of the file of the coordinate
     */
    public int getFileIndex() {
        return file.ordinal();
    }

    /**
     * Returns the index of the rank of the coordinate
     * <p>Semantically equivalent to <code>getRank().ordinal()</code></p>
     * @return the index of the rank of the coordinate
     */
    public int getRankIndex() {
        return rank.ordinal();
    }

    /**
     * Returns the traditional string representation of a coordinate
     * ex. "c4"
     * @return the traditional string representation of a coordinate
     */
    @Override
    public String toString() {
        return file.toString() + rank.toString();
    }

    /**
     * Returns <code>true</code> if <code>obj</code>, is a <code>Coordinate</code>, the files are equivalent, and the ranks are equivalent.
     * @param obj the object with which to compare against
     * @return <code>true</code> if equal
     */
    @Override
    public boolean equals(Object obj) {
        return
                obj instanceof final Coordinate otherCoordinate
                && file == otherCoordinate.file
                && rank == otherCoordinate.rank;
    }

    /**
     * Determine if this <code>Coordinate</code> corresponds to the
     * <code>coordinateString</code>.
     *
     * <p>For example, if <code>Coordinate coordinate = Coordinate.valueOf(File.B, Rank.SEVEN);</code>,</p>
     * <p>then <code>coordinate.is("b7");</code> is <code>true</code>.</p>
     *
     * <p><code>coordinateString</code> is <i>not</i> case-sensitive.</p>
     *
     * <p>This is equivalent to</p>
     * <p><code>this.equals(valueOf(coordinateString.toLowerCase()));</code></p>
     *
     * @param coordinateString a <code>String</code> representing a
     *                         coordinate
     * @return <code>true</code> if equivalent
     */
    public boolean is(final String coordinateString) {
        return this.equals(valueOf(coordinateString.toLowerCase()));
    }
}