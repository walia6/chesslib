package gg.w6.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import gg.w6.core.Position;

public class MoveGeneratorTest {
    @TestFactory
    List<DynamicTest> testGetLegalMoves() throws URISyntaxException, IOException {
        final List<DynamicTest> dynamicTests = new LinkedList<>();

        final Path pathToCSV = Path.of(getClass().getResource("/gg/w6/util/MoveGeneratorTest/testGetLegalMoves/perft.csv").toURI());
        final URI testSourceUri = pathToCSV.toUri();
        final List<String> lines = Files.readAllLines(pathToCSV);

        if (!lines.get(0).equals("fen,depth,perft")) {
            throw new IllegalArgumentException("Invalid CSV header");
        }

        // skip the header
        lines.remove(0);

        for (final String line : lines) {
            final String[] fields = line.split(",");

            final String fen = fields[0];
            final int depth = Integer.parseInt(fields[1]);
            final long expected = Long.parseLong(fields[2]);

            dynamicTests.add(DynamicTest.dynamicTest("[perft] depth=" + depth + " | FEN='" + fen + "'", testSourceUri, 
                () -> {
                    final Position position = Position.valueOf(fen);
                    final long actual = Perft.perft(depth, position);
                    assertEquals(expected, actual);
                }
            ));

        }
        return dynamicTests;
    }

}
