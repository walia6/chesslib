package gg.w6.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
     * Bounds for the offset cache.
     * Chessboard max move is at most 7 files/ranks away.
     */
    private static final int MIN = -Math.max(File.COUNT, Rank.COUNT) + 1;
    private static final int MAX = Math.max(File.COUNT, Rank.COUNT) - 1;

    /**
     * Precomputed cache of Offset instances.
     * Accessed by [files - MIN][ranks - MIN].
     */
    private static final Offset[][] CACHE = new Offset[MAX - MIN + 1][MAX - MIN + 1];

    static {
        for (int files = MIN; files <= MAX; files++) {
            for (int ranks = MIN; ranks <= MAX; ranks++) {
                CACHE[files - MIN][ranks - MIN] = new Offset(files, ranks);
            }
        }
    }

    /**
     * Creates or retrieves an Offset instance for the given files and ranks.
     *
     * @param files The number of files (horizontal movement).
     * @param ranks The number of ranks (vertical movement).
     * @return Cached Offset instance or new one if out of bounds.
     */
    public static Offset valueOf(final int files, final int ranks) {
        try {
            return CACHE[files - MIN][ranks - MIN];
        } catch (ArrayIndexOutOfBoundsException e) {
            return new Offset(files, ranks);
        }
    }

    /**
     * Number of files (horizontal movement).
     */
    private final int files;

    /**
     * Number of ranks (vertical movement).
     */
    private final int ranks;

    /**
     * Private constructor — use valueOf to create instances.
     */
    private Offset(final int files, final int ranks) {
        this.files = files;
        this.ranks = ranks;
    }

    public int getFiles() { return files; }

    public int getRanks() { return ranks; }

    /**
     * Lazily extends the offset from the given origin coordinate by the specified range.
     * Stops iteration if out of bounds.
     *
     * @param origin The origin coordinate.
     * @param range  The maximum number of steps.
     * @return Iterable of coordinates.
     */
    public Iterable<Coordinate> extendFrom(final Coordinate origin, final int range) {
        return () -> new Iterator<>() {
            private int steps = 1;
            private Coordinate next = computeNext();

            private Coordinate computeNext() {
                if (steps > range) return null;
                Coordinate coordinate = scale(steps).applyTo(origin);
                if (coordinate == null) return null;
                steps++;
                return coordinate;
            }

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Coordinate next() {
                Coordinate current = next;
                next = computeNext();
                return current;
            }
        };
    }

    /**
     * Eagerly computes the extension list (pre-existing behavior).
     *
     * @param origin The origin coordinate.
     * @param range  The maximum number of steps.
     * @return List of coordinates.
     */
    public List<Coordinate> computeExtension(final Coordinate origin, final int range) {
        final List<Coordinate> coordinates = new LinkedList<>();
        for (int steps = 1; steps <= range; steps++) {
            final Coordinate coordinate = scale(steps).applyTo(origin);
            if (coordinate == null) break;
            coordinates.add(coordinate);
        }
        return coordinates;
    }

    /**
     * Applies this offset to the given coordinate.
     *
     * @param origin The origin coordinate.
     * @return New coordinate or null if out of bounds.
     */
    public Coordinate applyTo(final Coordinate origin) {
        final int newFileIndex = origin.getFile().ordinal() + files;
        final int newRankIndex = origin.getRank().ordinal() + ranks;

        return newFileIndex > File.COUNT - 1
                || newRankIndex > Rank.COUNT - 1
                || newFileIndex < 0
                || newRankIndex < 0
                ? null
                : Coordinate.valueOf(newFileIndex, newRankIndex);
    }

    /**
     * Scales this offset by the given factor.
     *
     * @param factor The scaling factor.
     * @return New scaled Offset instance.
     */
    public Offset scale(final int factor) {
        return valueOf(files * factor, ranks * factor);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Offset otherOffset) {
            return files == otherOffset.files && ranks == otherOffset.ranks;
        }
        return false;
    }
}
