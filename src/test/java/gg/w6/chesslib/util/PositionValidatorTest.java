package gg.w6.chesslib.util;

import gg.w6.chesslib.model.Position;
import gg.w6.chesslib.util.PositionValidator.Legality;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionValidatorTest {


    @Test
    void testPositionValidatorConstructorCoverage() {
        // Just ensure the class loads
        assertDoesNotThrow(() -> Class.forName("gg.w6.chesslib.util.PositionValidator"));
    }


    @Test
    void testValidPosition() {
        final String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        final Position position = Position.valueOf(fen);
        final Legality legality = PositionValidator.getLegality(position);

        assertEquals(Legality.LEGAL, legality);
    }

    @Test
    void testNoKings() {
        final String fen = "8/8/8/8/8/8/8/8 w - - 0 1";
        final Position position = Position.valueOf(fen);
        final Legality legality = PositionValidator.getLegality(position);

        assertEquals(Legality.MISSING_KING, legality);
    }

    @Test
    void testNoWhiteKing() {
        final String fen = "k7/8/8/8/8/8/8/8 w - - 0 1";
        final Position position = Position.valueOf(fen);
        final Legality legality = PositionValidator.getLegality(position);


        assertEquals(Legality.MISSING_KING, legality);
    }

    @Test
    void testNoBlackKing() {
        final String fen = "K7/8/8/8/8/8/8/8 w - - 0 1";
        final Position position = Position.valueOf(fen);
        final Legality legality = PositionValidator.getLegality(position);

        assertEquals(Legality.MISSING_KING, legality);
    }

    @Test
    void testTooManyWhiteKings() {
        final String fen = "8/8/8/8/8/8/4K3/4K3 w - - 0 1";
        final Position position = Position.valueOf(fen);
        final Legality legality = PositionValidator.getLegality(position);

        assertEquals(Legality.TOO_MANY_KINGS, legality);
    }

    @Test
    void testTooManyBlackKings() {
        final String fen = "8/8/8/8/7k/8/4k3/4K3 w - - 0 1";
        final Position position = Position.valueOf(fen);
        final Legality legality = PositionValidator.getLegality(position);

        assertEquals(Legality.TOO_MANY_KINGS, legality);
    }

    @Test
    void testPawnOnFirstRank() {
        final String fen = "8/8/8/8/8/8/8/P2K3k w - - 0 1";
        final Position position = Position.valueOf(fen);
        final Legality legality = PositionValidator.getLegality(position);

        assertEquals(Legality.ILLEGAL_PAWN_RANK, legality);
    }

    @Test
    void testPawnOnEighthRank() {
        final String fen = "P7/8/8/8/7K/8/8/7k w - - 0 1";
        final Position position = Position.valueOf(fen);
        final Legality legality = PositionValidator.getLegality(position);

        assertEquals(Legality.ILLEGAL_PAWN_RANK, legality);
    }

    @Test
    void testKingInCheckByPawn() {
        final String fen = "7k/8/8/8/4p3/3K4/8/8 b - - 0 1";
        final Position position = Position.valueOf(fen);
        final Legality legality = PositionValidator.getLegality(position);

        assertEquals(Legality.CAN_CAPTURE_KING, legality);
    }

    @Test
    void testKingInCheckByRook() {
        final String fen = "8/8/8/8/8/8/8/r3K2k b - - 0 1"; // black rook attacks white king
        final Position position = Position.valueOf(fen);
        final Legality legality = PositionValidator.getLegality(position);

        assertEquals(Legality.CAN_CAPTURE_KING, legality);
    }

    @Test
    void testCallingGetErrorMessageOnLegalThrows() {
        final String fen = "8/8/8/8/8/8/8/4K2k w - - 0 1";
        final Position position = Position.valueOf(fen);
        final Legality legality = PositionValidator.getLegality(position);

        assertEquals(Legality.LEGAL, legality);
    }

    @Test
    void testVariousPositions() {
        final String[] legalPositions = {
            "7k/5Q2/8/8/8/8/8/K7 b - - 0 1", 
            "8/8/3Q4/3p4/3k4/8/8/K7 w - - 0 1", 
            "8/8/5Q2/4p3/3k4/8/8/K7 w - - 0 1" ,
            "8/8/8/8/3kpQ2/8/8/K7 w - - 0 1" ,
            "8/8/8/8/3k4/4p3/5Q2/K7 w - - 0 1", 
            "8/8/8/8/3k4/3p4/3Q4/K7 w - - 0 1" ,
            "8/8/8/8/3k4/2p5/1Q6/K7 w - - 0 1", 
            "8/8/8/8/1Qpk4/8/8/K7 w - - 0 1", 
            "8/8/1Q6/2p5/3k4/8/8/K7 w - - 0 1", 
            "8/8/8/5P2/4k3/8/8/K7 w - - 0 1", 
            "8/8/8/3P4/4k3/8/8/K7 w - - 0 1", 
            "8/8/8/8/4K3/5p2/8/k7 b - - 0 1", 
            "8/8/8/8/4K3/3p4/8/k7 b - - 0 1", 
            "qb1q2n1/bqn2n2/2q2q2/3n1r2/r1b1K3/3ppr2/6n1/k6q b - - 0 1"
        };

        final String[] illegalPositions = {
            "8/8/3Q4/8/3k4/8/8/K7 w - - 0 1", 
            "8/8/5Q2/8/3k4/8/8/K7 w - - 0 1", 
            "8/8/8/8/3k1Q2/8/8/K7 w - - 0 1", 
            "8/8/8/8/3k4/8/5Q2/K7 w - - 0 1", 
            "8/8/8/8/3k4/8/3Q4/K7 w - - 0 1", 
            "8/8/8/8/3k4/8/1Q6/K7 w - - 0 1", 
            "8/8/8/8/1Q1k4/8/8/K7 w - - 0 1", 
            "8/8/1Q6/8/3k4/8/8/K7 w - - 0 1", 
            "8/8/8/5N2/3k4/8/8/K7 w - - 0 1", 
            "8/8/8/8/3k4/5N2/8/K7 w - - 0 1", 
            "8/8/8/8/3k4/8/4N3/K7 w - - 0 1", 
            "8/8/8/8/3k4/8/2N5/K7 w - - 0 1", 
            "8/8/8/8/3k4/1N6/8/K7 w - - 0 1", 
            "8/8/8/1N6/3k4/8/8/K7 w - - 0 1", 
            "8/8/2N5/8/3k4/8/8/K7 w - - 0 1", 
            "8/8/4N3/8/3k4/8/8/K7 w - - 0 1", 
            "8/8/8/2pppN2/2pkp3/2ppp3/8/K7 w - - 0 1", 
            "8/8/8/2ppp3/2pkp3/2pppN2/8/K7 w - - 0 1", 
            "8/8/8/2ppp3/2pkp3/2ppp3/4N3/K7 w - - 0 1", 
            "8/8/8/2ppp3/2pkp3/2ppp3/2N5/K7 w - - 0 1", 
            "8/8/8/2ppp3/2pkp3/1Nppp3/8/K7 w - - 0 1", 
            "8/8/8/1Nppp3/2pkp3/2ppp3/8/K7 w - - 0 1", 
            "8/8/2N5/2ppp3/2pkp3/2ppp3/8/K7 w - - 0 1", 
            "8/8/4N3/2ppp3/2pkp3/2ppp3/8/K7 w - - 0 1", 
            "8/8/8/8/4k3/3P4/8/K7 w - - 0 1", 
            "8/8/8/8/4k3/5P2/8/K7 w - - 0 1", 
            "8/8/8/3p4/4K3/8/8/k7 b - - 0 1", 
            "8/8/8/5p2/4K3/8/8/k7 b - - 0 1", 
            "qb1q2n1/bqn2n2/2q2q2/3n1r2/r3K3/3ppr2/6n1/k6q b - - 0 1", 
            "8/8/4k3/8/4K3/8/8/2p5 w - - 0 1", 
            "2p5/8/4k3/8/4K3/8/8/8 w - - 0 1", 
            "2P5/8/4k3/8/4K3/8/8/8 w - - 0 1", 
            "8/8/4k3/8/4K3/8/8/2P5 w - - 0 1", 
            "8/8/4k3/8/4K3/8/8/2p5 b - - 0 1", 
            "2p5/8/4k3/8/4K3/8/8/8 b - - 0 1", 
            "2P5/8/4k3/8/4K3/8/8/8 b - - 0 1", 
            "8/8/4k3/8/4K3/8/8/2P5 b - - 0 1",
        };

        for (final String fen : legalPositions) {
            final Position position = Position.valueOf(fen);
            final Legality actual = PositionValidator.getLegality(position);

            assertEquals(Legality.LEGAL, actual);
        }

        for (final String fen : illegalPositions) {
            final Position position = Position.valueOf(fen);
            final Legality actual = PositionValidator.getLegality(position);

            assertNotEquals(Legality.LEGAL, actual);
        }
    }
}
