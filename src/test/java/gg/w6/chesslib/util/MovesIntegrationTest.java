package gg.w6.chesslib.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import gg.w6.chesslib.core.Position;

public class MovesIntegrationTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/states/king_in_check.csv", numLinesToSkip = 1)
    void testIsKingToMoveInCheckIntegration (String fen, boolean expected) {
        final Position position = Position.valueOf(fen);
        final boolean actual = Positions.isKingToMoveInCheck(position);

        assertEquals(expected, actual);
    }
}
