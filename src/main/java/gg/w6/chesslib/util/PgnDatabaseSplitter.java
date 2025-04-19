package gg.w6.chesslib.util;

import java.io.*;
import java.util.*;

public class PgnDatabaseSplitter implements Iterator<String>, Iterable<String>, AutoCloseable {
    private final BufferedReader reader;
    private String nextChunk;

    public PgnDatabaseSplitter(File file) throws IOException {
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

    @Override
    public boolean hasNext() {
        return nextChunk != null;
    }

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

    @Override
    public Iterator<String> iterator() {
        return this;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
