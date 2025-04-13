package gg.w6.chesslib.util;

import gg.w6.chesslib.core.Position;
import gg.w6.chesslib.core.Square;
import gg.w6.chesslib.piece.Piece;

public class FenParser {

    private FenParser() {
    } // ensure non-instantiability

    public static Position parse(final String fen) {

        final String[] recordFields = getAndVerifyRecordFields(fen);

        final Square[][] squares = new Square[File.COUNT][Rank.COUNT];
        try {
            parseAndPopulateSquares(recordFields, squares);
        } catch (final Exception e) {
            throw new IllegalArgumentException(
                    "Malformed piece placement field \""
                            + recordFields[0] + "\".");
        }

        return new Position(
                squares,
                new CastlingRights(
                        recordFields[2].contains("K"),
                        recordFields[2].contains("Q"),
                        recordFields[2].contains("k"),
                        recordFields[2].contains("q")),
                recordFields[3].equals("-")
                        ? null : Coordinate.valueOf(recordFields[3]),
                Color.valueOf(recordFields[1].charAt(0)),
                Integer.parseInt(recordFields[4]),
                Integer.parseInt(recordFields[5]));

    }

    private static String[] getAndVerifyRecordFields(String fen) {
        final String[] recordFields = fen.split(" ");

        if (recordFields.length != 6)
            throw new IllegalArgumentException(
                    "Malformed FEN. Too " +
                            (recordFields.length > 6 ? "many" : "few")
                            + " fields for FEN \"" + fen + "\".");

        // Validate castling rights field:
        if (!(recordFields[2].equals("-")
                || recordFields[2].matches("K?Q?k?q?")))
            throw new IllegalArgumentException(
                    "Malformed castling rights field \""
                            + recordFields[2] + "\".");

        // Validate active color field:
        if (!recordFields[1].matches("^[bBwW]$"))
            throw new IllegalArgumentException(
                    "Malformed color field \"" + recordFields[1] + "\".");

        // Validate halfmove clock field:
        if (!recordFields[4].matches("^\\d+$"))
            throw new IllegalArgumentException(
                    "Malformed halfmove clock field \""
                            + recordFields[4] + "\".");

        // Validate fullmove number field:
        if (!recordFields[5].matches("^\\d+$"))
            throw new IllegalArgumentException(
                    "Malformed fullmove number field \""
                            + recordFields[5] + "\".");
        return recordFields;
    }

    private static void parseAndPopulateSquares(final String[] recordFields,
                                                final Square[][] squares) {
        final String[] piecePlacementDataStrings = recordFields[0]
                .split("/");
        for (int i = 0, j = piecePlacementDataStrings.length - 1; i < j; i++, j--) {
            String temp = piecePlacementDataStrings[i];
            piecePlacementDataStrings[i] = piecePlacementDataStrings[j];
            piecePlacementDataStrings[j] = temp;
        }


        for (int i = 0; i < piecePlacementDataStrings.length; i++) {
            final String[] fields =
                    piecePlacementDataStrings[i].split("");
            for (int j = 0; j < fields.length; j++) {
                if (fields[j].matches("\\d")) {
                    fields[j] = "1".repeat(Integer.parseInt(fields[j]));
                }
            }
            piecePlacementDataStrings[i] = String.join("", fields);
            if (piecePlacementDataStrings[i].length() != Rank.COUNT) {
                throw new IllegalStateException();
            }
        }

        for (int rankIndex = 0; rankIndex < Rank.COUNT; rankIndex++) {
            for (int fileIndex = 0; fileIndex < File.COUNT; fileIndex++) {
                squares[fileIndex][rankIndex] =
                        piecePlacementDataStrings[rankIndex]
                                .charAt(fileIndex) == '1'
                                ? Square.valueOf(fileIndex, rankIndex, null)
                                : Square.valueOf(fileIndex, rankIndex, Piece.valueOf(
                                piecePlacementDataStrings[rankIndex]
                                        .charAt(fileIndex)));
            }
        }
    }
}
