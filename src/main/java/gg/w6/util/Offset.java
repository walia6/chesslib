package gg.w6.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents an offset on a chessboard.
 *
 * <p>Offsets are used to define the movement of pieces in different directions.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Offset offset = Offset.valueOf(1, 2);
 * }</pre>
 */
public class Offset {
    /**
     * Cache for storing previously created Offset instances.
     * This is used to avoid creating multiple instances of the same Offset.
     * The cache is thread-safe and uses a ConcurrentHashMap for efficient access.
     * The cache is cleared when the resetCache() method is called.
     * This is useful for testing purposes or when you want to free up memory.
     */
    private static final Map<Key, Offset> CACHE = new ConcurrentHashMap<>();

    /**
     * Creates a new Offset instance with the specified number of files and ranks.
     * If an Offset with the same files and ranks already exists in the cache, it returns that instance.
     *
     * @param files The number of files (horizontal movement).
     * @param ranks The number of ranks (vertical movement).
     * @return A new or cached Offset instance.
     */
    public static Offset valueOf(final int files, final int ranks) {
        return CACHE.computeIfAbsent(new Key(files, ranks), k -> new Offset(k.files, k.ranks));
    }

    /**
     * Resets the cache of Offset instances.
     * This is useful for testing purposes or when you want to free up memory.
     * This method is not thread-safe and should be used with caution.
     */
    public static void resetCache() {
        for (Key key : CACHE.keySet()) CACHE.remove(key);
    }

    /**
     * The number of files (horizontal movement).
     */
    private final int files;

    /**
     * The number of ranks (vertical movement).
     */
    private final int ranks;

    /**
     * Private constructor to create a new Offset instance.
     * This constructor is private to ensure that instances are created only through the valueOf method.
     *
     * @param files The number of files (horizontal movement).
     * @param ranks The number of ranks (vertical movement).
     */
    private Offset(final int files, final int ranks) {
        this.files = files;
        this.ranks = ranks;
    }

    /**
     * Returns the number of files (horizontal movement).
     *
     * @return The number of files.
     */
    public int getFiles() { return files; }

    /**
     * Returns the number of ranks (vertical movement).
     *
     * @return The number of ranks.
     */
    public int getRanks() { return ranks; }

    private record Key(int files, int ranks) {}

    /**
     * Extends the offset from the given origin coordinate by the specified range.
     * This method generates a list of coordinates that are reachable from the origin
     * by applying the offset multiple times.
     * For example, if the offset is (1, 2) and the origin is (e4),
     * the method will return a list of coordinates like (f6), (g8), etc.
     * 
     * <p>Note: The method will stop adding coordinates to the list if the resulting coordinate
     * is outside the board boundaries.</p>
     *
     * @param origin The origin coordinate from which to extend.
     * @param range  The maximum number of steps to extend.
     * @return A list of coordinates reachable from the origin by extending the offset.
     */
    public List<Coordinate> extendFrom(final Coordinate origin,
            final int range) {

        final List<Coordinate> coordinates = new LinkedList<>();
        
        for (int steps = 1; steps <= range; steps++) {
            final Coordinate coordinate = scale(steps).applyTo(origin);
            if (coordinate == null) break;
            coordinates.add(coordinate);
        }

        return coordinates;
    }

    /**
     * Applies the offset to the given origin coordinate.
     * This method calculates a new coordinate by adding the offset to the origin.
     * If the resulting coordinate is outside the board boundaries, it returns null.
     * 
     * @param origin The origin coordinate to which the offset will be applied.
     * @return The new coordinate after applying the offset, or null if out of bounds.
     */
    public Coordinate applyTo(final Coordinate origin) {

        final int newFileIndex = origin.getFile().ordinal() + files;
        final int newRankIndex = origin.getRank().ordinal() + ranks;

        return newFileIndex > File.COUNT - 1
                || newRankIndex > Rank.COUNT - 1
                || newFileIndex < 0
                || newRankIndex < 0
                ? null : Coordinate.valueOf(newFileIndex, newRankIndex);
    }

    /**
     * Scales the offset by the specified factor.
     * This method multiplies both the files and ranks of the offset by the given factor.
     * 
     * @param factor The factor by which to scale the offset.
     * @return A new Offset instance with scaled files and ranks.
     */
    public Offset scale(final int factor) {
        return valueOf(files * factor, ranks * factor);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Offset) {
            Offset otherOffset = (Offset) obj;
            return files == otherOffset.files && ranks == otherOffset.ranks;
        }
        return false;
    }
}
