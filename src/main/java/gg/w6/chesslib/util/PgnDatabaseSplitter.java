package gg.w6.chesslib.util;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An {@link Iterator} and {@link Iterable} for reading individual games from a PGN (Portable Game Notation) file.
 *
 * <p>This class splits a PGN database into individual game strings. It supports
 * iteration over the games and implements {@link AutoCloseable} for proper
 * resource management. Each element returned by the iterator is a single
 * complete PGN game, including its metadata and move list.</p>
 *
 * <p>Usage example:</p>
 * <pre><code>
 * try (PgnDatabaseSplitter splitter = new PgnDatabaseSplitter(new File("games.pgn"))) {
 *     for (String pgnString : splitter) {
 *         Game game = PgnParser.parse(pgnString);
 *     }
 * }
 * </code></pre>
 *
 * <p>This class is not thread-safe.</p>
 */
public class PgnDatabaseSplitter implements Iterator<String>, Iterable<String>, AutoCloseable {
    private final BufferedReader reader;
    private String nextChunk;

    /**
     * Constructs a new <code>PgnDatabaseSplitter</code> for the specified PGN file.
     *
     * <p>Each call to {@link #next()} will return the next game in the file as a PGN-formatted {@link String}.</p>
     *
     * @param file the PGN file to read from
     * @throws IOException if an I/O error occurs opening the file
     */
    public PgnDatabaseSplitter(@NotNull final File file) throws IOException {
        this.reader = new BufferedReader(new FileReader(file));
        this.nextChunk = readNextChunk();
    }

    private String readNextChunk() throws IOException {
        StringBuilder chunk = new StringBuilder();
        String line;
        boolean inChunk = false;
        boolean seenBreakLine = false;

        while ((line = reader.readLine()) != null) {
            if (!inChunk) {
                if (line.trim().startsWith("[")) {
                    inChunk = true;
                    chunk.append(line).append('\n');
                }
                continue;
            }

            if (!seenBreakLine) {
                if (!line.trim().isEmpty() && !line.trim().startsWith("[")) {
                    seenBreakLine = true;
                }
            } else {
                chunk.append(line).append('\n'); // one more line after break
                break;
            }

            chunk.append(line).append('\n');
        }

        return !chunk.isEmpty() ? chunk.toString() : null;
    }

    /**
     * Returns {@code true} if there is another PGN game to read.
     *
     * @return {@code true} if there is another PGN game, {@code false} otherwise
     */
    @Override
    public boolean hasNext() {
        return nextChunk != null;
    }


    /**
     * Returns the next PGN game from the file.
     *
     * @return the next PGN game as a {@code String}
     * @throws NoSuchElementException if no more games are available
     * @throws UncheckedIOException if an I/O error occurs while reading
     */
    @Override
    public String next() {
        if (nextChunk == null) throw new NoSuchElementException();
        String result = nextChunk;
        try {
            nextChunk = readNextChunk();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return result;
    }

    /**
     * Returns an {@link Iterator} over the PGN games in the file.
     *
     * <p>Returns {@code this}, as the class itself implements {@code Iterator<String>}.</p>
     *
     * @return an iterator over PGN game strings
     */
    @Override
    @NotNull
    public Iterator<String> iterator() {
        return this;
    }

    /**
     * Closes the underlying reader and releases any system resources associated with it.
     *
     * @throws IOException if an I/O error occurs while closing the reader
     */
    @Override
    public void close() throws IOException {
        reader.close();
    }
}
