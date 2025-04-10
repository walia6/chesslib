package gg.w6.util;

import java.util.concurrent.RecursiveTask;

import gg.w6.core.Position;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

public class Perft {

    public static long perft(Position position, int depth) {
        ForkJoinPool pool = new ForkJoinPool();
        return pool.invoke(new PerftTask(position, depth));
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
            if (depth == 0) return 1L;

            var moves = MoveGenerator.getLegalMoves(position);
            if (depth == 1) return (long) moves.size();

            var tasks = new ArrayList<PerftTask>();
            for (Move move : moves) {
                Position newPosition = position.applyTo(move);
                tasks.add(new PerftTask(newPosition, depth - 1));
            }

            invokeAll(tasks);
            long nodes = 0;
            for (var task : tasks) {
                nodes += task.join();
            }
            return nodes;
        }
    }
}
