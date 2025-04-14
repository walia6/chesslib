package gg.w6.chesslib.util;

import java.util.concurrent.RecursiveTask;

import gg.w6.chesslib.model.Position;
import gg.w6.chesslib.model.Move;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.ArrayList;

public class Perft {

    public static long perft(int depth, Position position) {
        if (depth == 0) return 1L;

        var moves = MoveGenerator.getLegalMoves(position);
        if (depth == 1) return moves.size();

        new ForkJoinPool();
        var tasks = new ArrayList<PerftTask>();

        for (Move move : moves) {
            Position newPosition = position.applyTo(move);
            tasks.add(new PerftTask(newPosition, depth - 1));
        }

        tasks.forEach(ForkJoinTask::fork);

        long nodes = 0;
        for (var task : tasks) {
            nodes += task.join();
        }

        return nodes;
    }

    private static class PerftTask extends RecursiveTask<Long> {
        private final Position position;
        private final int depth;

        PerftTask(Position position, int depth) {
            this.position = position;
            this.depth = depth;
        }

        @Override
        protected Long compute() {
            return perftSequential(depth, position);
        }

        private long perftSequential(int depth, Position position) {
            if (depth == 0) return 1L;

            var moves = MoveGenerator.getLegalMoves(position);
            if (depth == 1) return moves.size();

            long nodes = 0;
            for (Move move : moves) {
                Position newPosition = position.applyTo(move);
                nodes += perftSequential(depth - 1, newPosition);
            }
            return nodes;
        }
    }
}
