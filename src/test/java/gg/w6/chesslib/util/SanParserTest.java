package gg.w6.chesslib.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import gg.w6.chesslib.model.Move;
import gg.w6.chesslib.model.MoveType;
import gg.w6.chesslib.model.Position;
import gg.w6.chesslib.util.jsonmappings.sanparser.testparse.ExpectedMove;
import gg.w6.chesslib.util.jsonmappings.sanparser.testparse.TestCase;
import gg.w6.chesslib.util.jsonmappings.sanparser.testparse.TestCaseFile;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class SanParserTest {

    private static final String PARSE_TEST_CASES_JSON_FILES_PATH = "/testcases/sanparsertest/positions/";

    @TestFactory
    List<DynamicTest> testGetLegalMoves() throws URISyntaxException, IOException {
        final URL pathToJsonFolderURL = getClass().getResource(PARSE_TEST_CASES_JSON_FILES_PATH);

        if (pathToJsonFolderURL == null) {
            throw new FileNotFoundException(PARSE_TEST_CASES_JSON_FILES_PATH);
        }

        final Path pathToJsonFolder = Path.of(pathToJsonFolderURL.toURI());
        final List<DynamicTest> dynamicTests = new ArrayList<>();
        final ObjectMapper objectMapper = new ObjectMapper();

        try(final Stream<Path> files = Files.list(pathToJsonFolder)) {
            for (final Path pathToFile : (Iterable<Path>) files::iterator) {
                final File file = pathToFile.toFile();
                if (!file.toString().endsWith(Json.FILE_EXTENSION)) {
                    continue;
                }

                final TestCaseFile testCaseFile = objectMapper.readValue(file, TestCaseFile.class);

                for (final TestCase testCase : testCaseFile.testCases) {
                    final Position startingPosition = Position.valueOf(testCase.start.fen);
                    final URI testSourceUri = file.toURI();
                    for (final ExpectedMove expectedMove : testCase.expected) {
                        final String displayName = "[" + file.getName() + "] san=\"" + expectedMove.san + "\" fen=\"" + testCase.start.fen + "\"";
                        final Executable executable = () -> {
                            final Move actualMove = SanParser.parse(expectedMove.san, startingPosition);
                            assertEquals(expectedMove.uci, actualMove.toString(), () -> {return "Incorrect UCI. san=\"" + expectedMove.san + "\" fen=\"" + testCase.start.fen + "\"";});
                            assertEquals(MoveType.valueOf(expectedMove.movetype), actualMove.getMoveType(), () -> {return "Incorrect MoveType. san=\"" + expectedMove.san + "\" fen=\"" + testCase.start.fen + "\"";});
                        };
                        dynamicTests.add(DynamicTest.dynamicTest(displayName, testSourceUri, executable));
                    }
                }
            }
        }
        return dynamicTests;
    }

}
