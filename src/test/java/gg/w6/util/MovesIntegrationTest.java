package gg.w6.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
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
        final boolean actual = Moves.isKingToMoveInCheck(position);

        assertEquals(expected, actual);
    }

    @TestFactory
    Stream<DynamicTest> testGetLegalMovesAndGenerateSANIntegration() throws Exception {
        // Load JSON file
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/gg/w6/util/MovesIntegrationTest/testGetLegalMovesAndGenerateSANIntegration/famous.json");
        TestCaseFile testCaseFile = mapper.readValue(is, TestCaseFile.class);
    
        return testCaseFile.testCases.stream()
            .map(testCase -> DynamicTest.dynamicTest(
                "Testing position: " + testCase.start.fen,
                () -> {
                    Position position = Position.valueOf(testCase.start.fen);
                    Set<Move> legalMoves = Moves.getLegalMoves(position);
    
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
    
                    assertEquals(
                        expectedSansWithFen,
                        generatedSansWithFen,
                        "Mismatch in expected and generated SAN+FEN moves for position: " + testCase.start.fen
                    );
                }
            ));
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
