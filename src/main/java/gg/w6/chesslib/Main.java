package gg.w6.chesslib;


import gg.w6.chesslib.model.*;
import gg.w6.chesslib.model.piece.*;
import gg.w6.chesslib.util.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {

        final PositionBuilder startingPositionBuilder = new PositionBuilder();

        // Pawns
        for (char file = 'a'; file <= 'h'; file++) {
            startingPositionBuilder.addPiece(new Pawn(Color.WHITE), Coordinate.valueOf(file + "2"));
            startingPositionBuilder.addPiece(new Pawn(Color.BLACK), Coordinate.valueOf(file + "7"));
        }

        // Rooks
        startingPositionBuilder.addPiece(new Rook(Color.WHITE), Coordinate.valueOf("a1"));
        startingPositionBuilder.addPiece(new Rook(Color.WHITE), Coordinate.valueOf("h1"));
        startingPositionBuilder.addPiece(new Rook(Color.BLACK), Coordinate.valueOf("a8"));
        startingPositionBuilder.addPiece(new Rook(Color.BLACK), Coordinate.valueOf("h8"));

        // Knights
        startingPositionBuilder.addPiece(new Knight(Color.WHITE), Coordinate.valueOf("b1"));
        startingPositionBuilder.addPiece(new Knight(Color.WHITE), Coordinate.valueOf("g1"));
        startingPositionBuilder.addPiece(new Knight(Color.BLACK), Coordinate.valueOf("b8"));
        startingPositionBuilder.addPiece(new Knight(Color.BLACK), Coordinate.valueOf("g8"));

        // Bishops
        startingPositionBuilder.addPiece(new Bishop(Color.WHITE), Coordinate.valueOf("c1"));
        startingPositionBuilder.addPiece(new Bishop(Color.WHITE), Coordinate.valueOf("f1"));
        startingPositionBuilder.addPiece(new Bishop(Color.BLACK), Coordinate.valueOf("c8"));
        startingPositionBuilder.addPiece(new Bishop(Color.BLACK), Coordinate.valueOf("f8"));

        // Queens
        startingPositionBuilder.addPiece(new Queen(Color.WHITE), Coordinate.valueOf("d1"));
        startingPositionBuilder.addPiece(new Queen(Color.BLACK), Coordinate.valueOf("d8"));

        // Kings
        startingPositionBuilder.addPiece(new King(Color.WHITE), Coordinate.valueOf("e1"));
        startingPositionBuilder.addPiece(new King(Color.BLACK), Coordinate.valueOf("e8"));



        File f = Path.of("/home/walia6/lichess_db_standard_rated_2014-07.pgn").toFile();
        try (PgnDatabaseSplitter pgnDatabaseSplitter = new PgnDatabaseSplitter(f)) {
            int i = 0;
            int mostPieces = 0;
            for (String gameString : pgnDatabaseSplitter) {
                Game game = PgnParser.parse(gameString);
                Position position = startingPositionBuilder.toPosition();



                for (String sanString : game.sanStrings()) {
                    // System.out.println(sanString);
                    final Move move = SanParser.parse(sanString, position);
                    position = position.applyTo(move);


                    if (sanString.equals(game.sanStrings().get(game.sanStrings().size() - 1)) && Positions.isStalemate(position)) {
                        // System.out.println(position.generateFEN());
                        int currentPieces = 0;
                        for (Square square : position) {
                            if (!square.isEmpty() && square.getPiece().getColor() == position.getToMove()) {
                                currentPieces++;
                            }
                        }
                        if (currentPieces > mostPieces) {
                            mostPieces = currentPieces;
                            System.out.println("NEW MAX: " + currentPieces + " pieces. fen=" + position.generateFEN());
                        } else if (currentPieces == mostPieces) {
                            System.out.println("NEW MATCH: " + currentPieces + " pieces. fen=" + position.generateFEN());
                        }
                    }
                    //System.out.println(position.generateFEN());
                }
            }
        }
    }
}