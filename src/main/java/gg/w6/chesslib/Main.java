package gg.w6.chesslib;


import gg.w6.chesslib.model.Game;
import gg.w6.chesslib.util.PgnDatabaseSplitter;
import gg.w6.chesslib.util.PgnParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        File f = Path.of("/home/walia6/lichess_db_standard_rated_2013-01.pgn").toFile();
        try (PgnDatabaseSplitter pgnDatabaseSplitter = new PgnDatabaseSplitter(f)) {
            int i = 0;
            for (String gameString : pgnDatabaseSplitter) {
                Game game = PgnParser.parse(gameString);
                System.out.println("NEXTGAME");
                System.out.println(game.rawPgn());
                System.out.println(i++);
            }
        }
    }
}