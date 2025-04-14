package gg.w6.chesslib.util;

import gg.w6.chesslib.model.*;
import gg.w6.chesslib.model.piece.Piece;

import java.util.regex.Pattern;

public final class FenParser {

    private static final Pattern CASTLING_RIGHTS_FIELD_PATTERN = Pattern.compile("K?Q?k?q?");
    private static final Pattern ACTIVE_COLOR_FIELD_PATTERN = Pattern.compile("^[bBwW]$");
    private static final Pattern HALFMOVE_CLOCK_FIELD_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern FULLMOVE_NUMBER_FIELD_PATTERN = Pattern.compile("^\\d+$");

    private FenParser() {
    }

    public static Position parse(final String fen) {
        final String[] recordFields = getAndVerifyRecordFields(fen);

        final Square[][] squares = new Square[File.COUNT][Rank.COUNT];
        parseAndPopulateSquares(recordFields[0], squares);

        final CastlingRights castlingRights = new CastlingRights(
                recordFields[2].contains("K"),
                recordFields[2].contains("Q"),
                recordFields[2].contains("k"),
                recordFields[2].contains("q")
        );

        final Coordinate enPassant = "-".equals(recordFields[3]) ? null : Coordinate.valueOf(recordFields[3]);

        return new Position(
                squares,
                castlingRights,
                enPassant,
                Color.valueOf(recordFields[1].charAt(0)),
                Integer.parseInt(recordFields[4]),
                Integer.parseInt(recordFields[5])
        );
    }

    private static String[] getAndVerifyRecordFields(final String fen) {
        final String[] fields = fen.split(" ");

        if (fields.length != 6) {
            throw new IllegalArgumentException("Malformed FEN. Too " + (fields.length > 6 ? "many" : "few") + " fields for FEN \"" + fen + "\".");
        }

        if (!(fields[2].equals("-") || CASTLING_RIGHTS_FIELD_PATTERN.matcher(fields[2]).matches())) {
            throw new IllegalArgumentException("Malformed castling rights field \"" + fields[2] + "\".");
        }

        if (!ACTIVE_COLOR_FIELD_PATTERN.matcher(fields[1]).matches()) {
            throw new IllegalArgumentException("Malformed color field \"" + fields[1] + "\".");
        }

        if (!HALFMOVE_CLOCK_FIELD_PATTERN.matcher(fields[4]).matches()) {
            throw new IllegalArgumentException("Malformed halfmove clock field \"" + fields[4] + "\".");
        }

        if (!FULLMOVE_NUMBER_FIELD_PATTERN.matcher(fields[5]).matches()) {
            throw new IllegalArgumentException("Malformed fullmove number field \"" + fields[5] + "\".");
        }

        return fields;
    }

    private static void parseAndPopulateSquares(final String piecePlacement, final Square[][] squares) {
        final String[] ranks = piecePlacement.split("/");
        if (ranks.length != Rank.COUNT) {
            throw new IllegalArgumentException("Malformed piece placement field \"" + piecePlacement + "\".");
        }

        for (int rankIndex = 0; rankIndex < Rank.COUNT; rankIndex++) {
            final String rank = ranks[Rank.COUNT - 1 - rankIndex]; // reverse ranks
            int fileIndex = 0;

            for (int i = 0, len = rank.length(); i < len; i++) {
                final char c = rank.charAt(i);

                if (c >= '1' && c <= '8') {
                    final int emptySquares = c - '0';
                    if (fileIndex + emptySquares > File.COUNT) {
                        throw new IllegalArgumentException("Too many squares in rank: \"" + piecePlacement + "\".");
                    }
                    for (int e = 0; e < emptySquares; e++) {
                        squares[fileIndex++][rankIndex] = Square.valueOf(fileIndex - 1, rankIndex, null);
                    }
                } else {
                    if (fileIndex >= File.COUNT) {
                        throw new IllegalArgumentException("Too many squares in rank: \"" + piecePlacement + "\".");
                    }
                    squares[fileIndex][rankIndex] = Square.valueOf(fileIndex, rankIndex, Piece.valueOf(c));
                    fileIndex++;
                }

            }

            if (fileIndex != File.COUNT) {
                throw new IllegalArgumentException("Malformed piece placement field \"" + piecePlacement + "\".");
            }
        }
    }
}

