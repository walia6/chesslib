package gg.w6;

import gg.w6.core.Position;
import gg.w6.util.Moves;
import gg.w6.util.Perft;

public class Main {

    public static void main(String[] args) {

        final Position startingPosition = Position.valueOf("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");

        System.out.println(Perft.perft(startingPosition, 5));


        /*
        System.out.println("test");

        Position position = Position.valueOf("Bn1N3R/ppPpNR1r/BnBr1NKR/k3pP2/3PR2R/N7/3P2P1/4Q2R w - - 0 1");

        Set<Move> moves = MoveGenerator.getLegalMoves(position);

        for (Move move : moves) {
            System.out.println(Moves.generateSAN(move, position));
        }

        System.out.println(moves.size()); */


        /*      String startingFEN = "r2qk2r/3n1pb1/p2p1np1/3Pp2p/Pp6/1N2BP2/1PPQB1PP/R3K2R w KQkq - 0 15";

        Position starting = Position.valueOf(startingFEN);

        

        assertThat(startingFEN.equals(starting.generateFEN()));
        // "c2-c4"
        Move white14 = Moves.generateMoveFromString(starting, "c2-c4");

        Position newPosition = starting.applyTo(white14);

        System.out.println(newPosition.generateFEN());
        System.out.println("Hello?");

        assertThat(newPosition.generateFEN().equals("r2qk2r/3n1pb1/p2p1np1/3Pp2p/PpP5/1N2BP2/1P1QB1PP/R3K2R b KQkq c3 0 15"));*/
    }
}