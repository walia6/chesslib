package gg.w6.chesslib.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import gg.w6.chesslib.model.Move;
import gg.w6.chesslib.util.jsonmappings.ExpectedMove;
import gg.w6.chesslib.util.jsonmappings.TestCase;
import gg.w6.chesslib.util.jsonmappings.TestCaseFile;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import gg.w6.chesslib.model.Position;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

public class MoveGeneratorTest {

    private static final String PERFT_TEST_CASES_CSV_FILE_PATH = "/perft/perft.csv";
    private static final String PERFT_TEST_CASES_CSV_FILE_HEADER = "fen,depth,perft";
    private static final String GET_LEGAL_MOVES_TEST_CASES_JSON_FILES_PATH = "/positions/";

    @TestFactory
    List<DynamicTest> generatePerftTests() throws URISyntaxException, IOException {
        final List<DynamicTest> dynamicTests = new ArrayList<>();
        final Path pathToCSV = Path.of(Objects.requireNonNull(getClass().getResource(PERFT_TEST_CASES_CSV_FILE_PATH)).toURI());
        final URI testSourceUri = pathToCSV.toUri();
        final List<String> lines = Files.readAllLines(pathToCSV);

        if (!lines.get(0).equals(PERFT_TEST_CASES_CSV_FILE_HEADER)) {
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

    @TestFactory
    List<DynamicTest> testGetLegalMoves() throws URISyntaxException, IOException {
        final URL pathToJsonFolderURL = getClass().getResource(GET_LEGAL_MOVES_TEST_CASES_JSON_FILES_PATH);
        if (pathToJsonFolderURL == null) {
            throw new FileNotFoundException(GET_LEGAL_MOVES_TEST_CASES_JSON_FILES_PATH);
        }
        final Path pathToJsonFolder = Path.of(pathToJsonFolderURL.toURI());

        final List<DynamicTest> dynamicTests = new ArrayList<>();
        final ObjectMapper objectMapper = new ObjectMapper();

        try(Stream<Path> files = Files.list(pathToJsonFolder)) {
            for (Path pathToFile : (Iterable<Path>) files::iterator) {
                final File file = pathToFile.toFile();
                if (!file.toString().endsWith(Json.FILE_EXTENSION)) {
                    continue;
                }

                final TestCaseFile testCaseFile = objectMapper.readValue(file, TestCaseFile.class);

                for (final TestCase testCase : testCaseFile.testCases) {
                    final Position startingPosition = Position.valueOf(testCase.start.fen);
                    final String displayName = "[" + file.getName() + "] FEN: \"" + testCase.start.fen + "\"";
                    final URI testSourceUri = file.toURI();
                    final Executable executable = () ->
                    {
                        final Set<Move> actualMoves = MoveGenerator.getLegalMoves(startingPosition);

                        final Set<String> actualMoveStrings = new HashSet<>();
                        final Set<String> expectedMoveStrings = new HashSet<>();

                        final Set<String> missingMoveStrings = new HashSet<>();
                        final Set<String> unexpectedMoveStrings = new HashSet<>();

                        for (final Move move : actualMoves) {
                            final String moveString = move.toString();
                            actualMoveStrings.add(moveString);
                            unexpectedMoveStrings.add(moveString);
                        }

                        for (final ExpectedMove expectedMove : testCase.expected) {
                            final String moveString = expectedMove.move;
                            expectedMoveStrings.add(moveString);
                            missingMoveStrings.add(moveString);
                        }

                        missingMoveStrings.removeAll(actualMoveStrings);
                        unexpectedMoveStrings.removeAll(expectedMoveStrings);

                        assertAll(
                                "Mismatch in expected and generated SAN+FEN moves for position: " + testCase.start.fen,
                                () -> assertTrue(missingMoveStrings.isEmpty(), "Missing moves:\n" + missingMoveStrings.stream().sorted().collect(Collectors.joining("\n"))),
                                () -> assertTrue(unexpectedMoveStrings.isEmpty(), "Unexpected moves:\n" + unexpectedMoveStrings.stream().sorted().collect(Collectors.joining("\n")))
                        );

                    };

                    dynamicTests.add(DynamicTest.dynamicTest(displayName, testSourceUri, executable));
                }
            }
        }
        return dynamicTests;
    }
}

