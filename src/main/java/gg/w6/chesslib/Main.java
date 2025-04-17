package gg.w6.chesslib;

import gg.w6.chesslib.model.*;
import gg.w6.chesslib.model.piece.*;
import gg.w6.chesslib.util.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        final PositionBuilder startingPositionBuilder = new PositionBuilder();

        // Add all starting pieces
        for (char file = 'a'; file <= 'h'; file++) {
            startingPositionBuilder.addPiece(new Pawn(Color.WHITE), Coordinate.valueOf(file + "2"));
            startingPositionBuilder.addPiece(new Pawn(Color.BLACK), Coordinate.valueOf(file + "7"));
        }

        startingPositionBuilder.addPiece(new Rook(Color.WHITE), Coordinate.valueOf("a1"));
        startingPositionBuilder.addPiece(new Rook(Color.WHITE), Coordinate.valueOf("h1"));
        startingPositionBuilder.addPiece(new Rook(Color.BLACK), Coordinate.valueOf("a8"));
        startingPositionBuilder.addPiece(new Rook(Color.BLACK), Coordinate.valueOf("h8"));

        startingPositionBuilder.addPiece(new Knight(Color.WHITE), Coordinate.valueOf("b1"));
        startingPositionBuilder.addPiece(new Knight(Color.WHITE), Coordinate.valueOf("g1"));
        startingPositionBuilder.addPiece(new Knight(Color.BLACK), Coordinate.valueOf("b8"));
        startingPositionBuilder.addPiece(new Knight(Color.BLACK), Coordinate.valueOf("g8"));

        startingPositionBuilder.addPiece(new Bishop(Color.WHITE), Coordinate.valueOf("c1"));
        startingPositionBuilder.addPiece(new Bishop(Color.WHITE), Coordinate.valueOf("f1"));
        startingPositionBuilder.addPiece(new Bishop(Color.BLACK), Coordinate.valueOf("c8"));
        startingPositionBuilder.addPiece(new Bishop(Color.BLACK), Coordinate.valueOf("f8"));

        startingPositionBuilder.addPiece(new Queen(Color.WHITE), Coordinate.valueOf("d1"));
        startingPositionBuilder.addPiece(new Queen(Color.BLACK), Coordinate.valueOf("d8"));

        startingPositionBuilder.addPiece(new King(Color.WHITE), Coordinate.valueOf("e1"));
        startingPositionBuilder.addPiece(new King(Color.BLACK), Coordinate.valueOf("e8"));

        File pgnFile = Path.of("/home/walia6/test.pgn").toFile();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Semaphore semaphore = new Semaphore(32); // Limit concurrency
        AtomicInteger maxPieces = new AtomicInteger(0);
        // add this alongside maxPieces
        AtomicInteger minStalematePly = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger gamesProcessed = new AtomicInteger(0);



        try (PgnDatabaseSplitter splitter = new PgnDatabaseSplitter(pgnFile)) {
            for (String gameString : splitter) {
                semaphore.acquire();
                executor.submit(() -> {
                    try {
                        Game game = PgnParser.parse(gameString);
                        Position position = startingPositionBuilder.toPosition();

                        var sanList = game.sanStrings();
                        for (int i = 0; i < sanList.size(); i++) {
                            Move move = SanParser.parse(sanList.get(i), position);
                            position = position.applyTo(move);

                            if (i == sanList.size() - 1 && Positions.isStalemate(position)) {
                                int count = 0;
                                for (Square square : position) {
                                    if (!square.isEmpty() && square.getPiece().getColor() == position.getToMove()) {
                                        count++;
                                    }
                                }

                                int prevMax;
                                do {
                                    prevMax = maxPieces.get();
                                    if (count > prevMax) {
                                        System.out.println("NEW MAX: " + count + " pieces. fen=" + position.generateFEN());
                                        System.out.println(game.rawPgn());
                                    } else if (count == prevMax) {
                                        System.out.println("NEW MATCH: " + count + " pieces. fen=" + position.generateFEN());
                                        System.out.println("test");
                                    }

                                } while (count > prevMax && !maxPieces.compareAndSet(prevMax, count));

                                int plies = i + 1;
                                int prevMin;
                                do {
                                    prevMin = minStalematePly.get();
                                    if (plies < prevMin) {
                                        System.out.println("QUICKEST STALEMATE: " + plies + " plies. fen=" + position.generateFEN());
                                        System.out.println(game.rawPgn());
                                    } else if (plies == prevMin) {
                                        System.out.println("TIE FOR QUICKEST STALEMATE: " + plies + " plies. fen=" + position.generateFEN());
                                    }

                                } while (plies < prevMin && !minStalematePly.compareAndSet(prevMin, plies));
                            }

                        }
                    } catch (Exception ignored) {
                    } finally {
                        int currentCount = gamesProcessed.incrementAndGet();
                        if (currentCount % 250000 == 0) {
                            LocalDateTime now = LocalDateTime.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            System.out.println("[" + now.format(formatter) + "] Processed " + currentCount + " games...");
                        }

                        semaphore.release();
                    }
                });
            }
        }

        executor.shutdown();
        executor.awaitTermination(7, TimeUnit.DAYS);
    }
}
