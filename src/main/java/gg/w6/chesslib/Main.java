package gg.w6.chesslib;



public class Main {

    public static void main(String[] args) {



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