package gg.w6.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import gg.w6.core.Position;

public class MovesIntegrationTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/gg/w6/util/MovesIntegrationTest/testIsKingToMoveInCheckIntegration.csv", numLinesToSkip = 1)
    void testIsKingToMoveInCheckIntegration (String fen, boolean expected) {
        final Position position = Position.valueOf(fen);
        final boolean actual = Positions.isKingToMoveInCheck(position);

        assertEquals(expected, actual);
    }


    @TestFactory
    Collection<DynamicTest> testGetLegalMovesAndGenerateSANIntegration() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        URL dir = getClass().getResource("/gg/w6/util/MovesIntegrationTest/testGetLegalMovesAndGenerateSANIntegration/");
        Path path = Path.of(dir.toURI());

        try (Stream<Path> files = Files.list(path)) {
            return files
                .filter(p -> p.toString().endsWith(".json"))
                .flatMap(file -> {
                    try {
                        TestCaseFile testCaseFile = mapper.readValue(file.toFile(), TestCaseFile.class);
                        return testCaseFile.testCases.stream()
                            .map(testCase -> DynamicTest.dynamicTest(
                                "[" + file.getFileName() + "] FEN: \"" + testCase.start.fen + "\"",
                                () -> {
                                    Position position = Position.valueOf(testCase.start.fen);
                                    Set<Move> legalMoves = MoveGenerator.getLegalMoves(position);

                                    Set<String> generatedSansWithFen = legalMoves.stream()
                                        .map(move -> {
                                            String san = Moves.generateSAN(move, position);
                                            String fen = position.applyTo(move).generateFEN();
                                            return san + " | " + fen;
                                        })
                                        .collect(Collectors.toSet());

                                    Set<String> expectedSansWithFen = testCase.expected.stream()
                                        .map(expected -> expected.move + " | " + expected.fen)
                                        .collect(Collectors.toSet());
                                    if (!expectedSansWithFen.equals(generatedSansWithFen)) {
    Set<String> missing = new HashSet<>(expectedSansWithFen);
    missing.removeAll(generatedSansWithFen);

    Set<String> unexpected = new HashSet<>(generatedSansWithFen);
    unexpected.removeAll(expectedSansWithFen);

    assertAll(
        "Mismatch in expected and generated SAN+FEN moves for position: " + testCase.start.fen,
        () -> assertTrue(missing.isEmpty(), "Missing moves:\n" + missing.stream().sorted().collect(Collectors.joining("\n"))),
        () -> assertTrue(unexpected.isEmpty(), "Unexpected moves:\n" + unexpected.stream().sorted().collect(Collectors.joining("\n")))
    );
}
                                }
                            ));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList()); // ← collect to list to avoid lazy stream issues
        }
    }


    

    // --- JSON mapping classes ---
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TestCaseFile {
        public String description; 
        public List<TestCase> testCases;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TestCase {
        public StartPosition start;
        public List<ExpectedMove> expected;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StartPosition {
        public String description;
        public String fen;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExpectedMove {
        public String move;
        public String fen;
    }
    
}
