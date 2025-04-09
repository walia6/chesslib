package gg.w6.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import gg.w6.piece.King;
import gg.w6.piece.Piece;
import gg.w6.util.CastlingRights;
import gg.w6.util.Color;
import gg.w6.util.Coordinate;
import gg.w6.util.File;
import gg.w6.util.Move;
import gg.w6.util.MoveType;
import gg.w6.util.Moves;
import gg.w6.util.Rank;

/**
 * Represents a chess position, including the arrangement of pieces on the
 * board, castling rights, active color, en passant target square, halfmove
 * clock, and fullmove number.
 */
public class Position {


    /**
     * Creates a {@code Position} object from a FEN string.
     * 
     * @param fen the FEN string representing the position
     * @return a Position object representing the position
     * @throws IllegalArgumentException if the FEN string is malformed
     */
    public static Position valueOf(final String fen) {

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

        final Square[][] squares = new Square[File.COUNT][Rank.COUNT];
        try {
            parseAndPopulateSquares(recordFields, squares);
        } catch (final Exception e) {
            throw new IllegalArgumentException(
                    "Malformed piece placement field \""
                            + recordFields[0] + "\".");
        }

        final Position position = new Position(
                squares,
                new CastlingRights(
                        recordFields[2].contains("K"),
                        recordFields[2].contains("Q"),
                        recordFields[2].contains("k"),
                        recordFields[2].contains("q")),
                recordFields[3].equals("-")
                        ? null : Coordinate.valueOf(recordFields[3]),
                Color.valueOf(recordFields[1].charAt(0)),
                Integer.valueOf(recordFields[4]),
                Integer.valueOf(recordFields[5]));



        return position;
    }

    /**
     * Parses the piece placement data from the FEN string and populates the
     * squares of the position with the corresponding pieces.
     * 
     * @param recordFields the fields of the FEN string
     * @param position     the Position object to populate
     */
    private static void parseAndPopulateSquares(final String[] recordFields,
            final Square[][] squares) {
        final String[] piecePlacementDataStrings = Arrays.asList(
                recordFields[0].split("/")).stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        lst -> {
                            Collections.reverse(lst);
                            return lst.toArray(new String[0]);
                        }));

        for (int i = 0; i < piecePlacementDataStrings.length; i++) {
            final String[] fields =
                    piecePlacementDataStrings[i].split("");
            for (int j = 0; j < fields.length; j++) {
                if (fields[j].matches("\\d")) {
                    fields[j] = "1".repeat(Integer.valueOf(fields[j]));
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


    /** 
     * The squares of the chessboard, represented as an 8x8 array.
     * Each square can contain a piece or be empty.
     */
    private final Square[][] squares;

    /**
     * The castling rights of the position, indicating which players can
     * castle kingside or queenside.
     */
    private final CastlingRights castlingRights;

    /**
     * The target square for en passant captures, or null if there is no
     * en passant target.
     */
    private final Coordinate enPassantTarget;

    /**
     * The color of the player who is to move next.
     */
    private final Color toMove;

    /**
     * The halfmove clock, which counts the number of halfmoves since the
     * last pawn move or capture.
     */
    private final int halfMoveClock;

    /**
     * The fullmove number, which counts the number of full moves in the
     * game.
     */
    private final int fullMoves;

    /**
     * Constructs a {@code Position} object with the specified castling rights,
     * en passant target square, active color, halfmove clock, and fullmove
     * number.
     * 
     * @param castlingRights   the castling rights of the position
     * @param enPassantTargets the target square for en passant captures
     * @param toMove           the color of the player who is to move next
     * @param halfMoveClock    the halfmove clock
     * @param fullMoves        the fullmove number
     */
    public Position(final Square[][] squares, final CastlingRights castlingRights,
            final Coordinate enPassantTargets, final Color toMove,
            final int halfMoveClock, final int fullMoves) {

        if (squares.length == File.COUNT){
            for (int fileIndex = 0; fileIndex < squares.length; fileIndex++) {
                if (squares[fileIndex].length != Rank.COUNT) {
                    throw new IllegalArgumentException("Malformed Squares[][]");
                }
                for (int rankIndex = 0; rankIndex < squares[0].length;
                        rankIndex++) {
                    if (squares[fileIndex][rankIndex] == null) {
                        throw new NullPointerException("Found a null square!");
                    } else {
                        final Square square = squares[fileIndex][rankIndex];
                        if (!square.getCoordinate().equals(Coordinate.valueOf(fileIndex, rankIndex))) {
                            throw new IllegalArgumentException(
                                    "Found a square with a non-matching"
                                    + " coordinate. Expected: (" + fileIndex + ", " + rankIndex + "). Got: (" + square.getCoordinate().getFile().ordinal() + ", " + square.getCoordinate().getRank().ordinal() + ")");
                        }
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Malformed Squares[][]");
        }

        this.squares = squares;
        this.castlingRights = castlingRights;
        this.enPassantTarget = enPassantTargets;
        this.toMove = toMove;
        this.halfMoveClock = halfMoveClock;
        this.fullMoves = fullMoves;
    }

    /**
     * Returns the square at the specified file and rank.
     * 
     * @param file the file of the square
     * @param rank the rank of the square
     * @return the square at the specified file and rank
     */
    public Square getSquare(final File file, final Rank rank) {
        return squares[file.ordinal()][rank.ordinal()];
    }

    /**
     * Returns the square at the specified file and rank.
     * 
     * @param fileIndex  the index of the file
     * @param rankIndex  the index of the rank
     * @return the square at the specified file and rank
     */
    public Square getSquare(final int fileIndex, final int rankIndex) {
        return squares[fileIndex][rankIndex];
    }

    /**
     * Returns the square at the specified coordinate.
     * 
     * @param coordinate the coordinate of the square
     * @return the square at the specified coordinate
     */
    public Square getSquare(final Coordinate coordinate) {
        return squares
                [coordinate.getFile().ordinal()]
                [coordinate.getRank().ordinal()];
    }

    /**
     * Returns the castling rights of the position.
     * 
     * @return the castling rights of the position
     */
    public CastlingRights getCastlingRights() {
        return castlingRights;
    }

    /**
     * Returns the target square for en passant captures.
     * 
     * @return the target square for en passant captures
     */
    public Coordinate getEnPassantTarget() {
        return enPassantTarget;
    }

    /**
     * Returns the color of the player who is to move next.
     * 
     * @return the color of the player who is to move next
     */
    public Color getToMove() {
        return toMove;
    }

    /**
     * Returns the fullmove number, which counts the number of full moves
     * in the game.
     * 
     * @return the fullmove number
     */
    public int getFullMoves() {
        return fullMoves;
        
    }

    /**
     * Returns the halfmove clock, which counts the number of halfmoves
     * since the last pawn move or capture.
     * 
     * @return the halfmove clock
     */
    public int getHalfMoveClock() {
        return halfMoveClock;
    }

    /**
     * Generates the FEN (Forsyth-Edwards Notation) string representing the current board state.
     *
     * <p>FEN is a standard notation for describing a particular position of a chess game.
     * It is widely used in chess engines, databases, and communication protocols for 
     * encoding board states in a single-line format. FEN strings contain six fields separated 
     * by spaces:</p>
     *
     * <ol>
     *   <li><b>Piece placement</b>: Describes the position of each piece on the board,
     *       starting from the 8th rank to the 1st rank. Empty squares are represented 
     *       by digits 1–8 (number of empty squares), and pieces are represented by 
     *       letters ('P', 'N', 'B', 'R', 'Q', 'K' for white; lowercase for black).</li>
     *   <li><b>Active color</b>: Indicates which player is to move next, 'w' for white, 'b' for black.</li>
     *   <li><b>Castling availability</b>: Letters 'KQkq' for white/black kingside/queenside castling rights,
     *       or '-' if none are available.</li>
     *   <li><b>En passant target square</b>: The square that can be captured via en passant, or '-' if not applicable.</li>
     *   <li><b>Halfmove clock</b>: The number of halfmoves since the last capture or pawn move, used for the 50-move rule.</li>
     *   <li><b>Fullmove number</b>: The number of the full move, starting at 1 and incremented after each black move.</li>
     * </ol>
     *
     * <p>Example FEN string for the starting position:</p>
     * <pre>
     * rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
     * </pre>
     *
     * <p>For more details, see: 
     * <a href="https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation">FEN on Wikipedia</a></p>
     *
     * @return A string in FEN format representing the current state of the chess position.
     */
    public String generateFEN() {

        final StringBuilder fenStringBuilder = new StringBuilder();

        appendRankPiecePlacementDataString(fenStringBuilder);

        fenStringBuilder.append(" ");

        // Active color
        fenStringBuilder.append(toMove == Color.WHITE ? "w" : "b");

        fenStringBuilder.append(" ");

        // Castling availability
        if (!castlingRights.whiteKingside()
                && !castlingRights.whiteQueenside()
                && !castlingRights.blackKingside()
                && !castlingRights.blackQueenside()) {
            fenStringBuilder.append("-");
        } else {
            if (castlingRights.whiteKingside())
                fenStringBuilder.append("K");
            if (castlingRights.whiteQueenside())
                fenStringBuilder.append("Q");
            if (castlingRights.blackKingside())
                fenStringBuilder.append("k");
            if (castlingRights.blackQueenside())
                fenStringBuilder.append("q");
        }

        fenStringBuilder.append(" ");

        // En passant target square
        fenStringBuilder.append(
                enPassantTarget == null ? "-" : enPassantTarget);

        fenStringBuilder.append(" ");

        // Halfmove clock
        fenStringBuilder.append(halfMoveClock);

        fenStringBuilder.append(" ");

        // Fullmove number
        fenStringBuilder.append(fullMoves);

        return fenStringBuilder.toString();
    }

    /**
     * Appends the rank piece placement data string to the FEN string.
     *
     * @param fenStringBuilder the StringBuilder to append the FEN string to
     */
    private void appendRankPiecePlacementDataString(
            final StringBuilder fenStringBuilder) {
        // piece placement data
        final StringBuilder[] rankPiecePlacementDataStringBuilders =
                new StringBuilder[Rank.COUNT];
        for (int i = 0; i < Rank.COUNT; i++) {
            rankPiecePlacementDataStringBuilders[i] = new StringBuilder();
        }

        // go thru each rank
        for (int reverseRankIndex = 0; reverseRankIndex < Rank.COUNT;
                reverseRankIndex++) {
            int emptySquaresInARow = 0;
            for (int fileIndex = 0; fileIndex < File.COUNT; fileIndex++) {

                // in reverse;
                final Piece piece = this.getSquare(fileIndex,
                        Rank.COUNT - reverseRankIndex - 1).getPiece();

                final StringBuilder rankPiecePlacementDataStringBuilder =
                        rankPiecePlacementDataStringBuilders[reverseRankIndex];

                // if there is no piece, increment emptySquareInARow.
                if (piece == null) {
                    emptySquaresInARow++;

                    // if this is the last file, append it to
                    // rankPiecePlacementDataStringBuilder
                    if (fileIndex == File.COUNT - 1) {
                        rankPiecePlacementDataStringBuilder
                                .append(emptySquaresInARow);
                    }
                    continue;
                }

                // there is a piece. if emptySquaresInARow != 0, append it to
                // the rankPiecePlacementDataStringBuilder
                // and set it to 0.
                if (emptySquaresInARow != 0) {
                    rankPiecePlacementDataStringBuilder
                            .append(emptySquaresInARow);
                    emptySquaresInARow = 0;
                }

                // append our piece's letter
                rankPiecePlacementDataStringBuilder.append(piece.getLetter());
            }
        }

        for (int i = 0; i < Rank.COUNT - 1; i++) {
            fenStringBuilder.append(
                    rankPiecePlacementDataStringBuilders[i].toString());
            fenStringBuilder.append("/");
        }

        fenStringBuilder.append(rankPiecePlacementDataStringBuilders
                [Rank.COUNT - 1].toString());
    }

    public Position applyTo(final Move move) {

        final Square[][] newSquares = this.squares.clone();

        for (int fileIndex = 0; fileIndex < File.COUNT; fileIndex++) {
            newSquares[fileIndex] = this.squares[fileIndex].clone();
        }

        final int fromFileIndex = move.getFrom().getFile().ordinal();
        final int fromRankIndex = move.getFrom().getRank().ordinal();
        final int toFileIndex = move.getTo().getFile().ordinal();
        final int toRankIndex = move.getTo().getRank().ordinal();

        final MoveType moveType = move.getMoveType();
        final Piece movedPiece = squares[fromFileIndex][fromRankIndex].getPiece();
        
        switch (move.getMoveType()) {
            case NORMAL -> {
                newSquares[fromFileIndex][fromRankIndex] = Square.valueOf(fromFileIndex, fromRankIndex, null);
                newSquares[toFileIndex][toRankIndex] = Square.valueOf(toFileIndex, toRankIndex, movedPiece);
            }
            case CASTLING -> {
                newSquares[fromFileIndex][fromRankIndex] = Square.valueOf(fromFileIndex, fromRankIndex, null);
                newSquares[toFileIndex][toRankIndex] = Square.valueOf(toFileIndex, toRankIndex, movedPiece);
            
                int rank = toRankIndex;
                int rookFromFile, rookToFile;
            
                if (toFileIndex == 6) { // kingside (e.g., e1 to g1 or e8 to g8)
                    rookFromFile = 7;
                    rookToFile = 5;
                } else if (toFileIndex == 2) { // queenside (e.g., e1 to c1 or e8 to c8)
                    rookFromFile = 0;
                    rookToFile = 3;
                } else {
                    throw new IllegalArgumentException("Invalid king destination for castling: file " + toFileIndex);
                }
            
                newSquares[rookFromFile][rank] = Square.valueOf(rookFromFile, rank, null);
                newSquares[rookToFile][rank] = Square.valueOf(rookToFile, rank, this.squares[rookFromFile][rank].getPiece());
            
                if (rank != 0 && rank != 7) {
                    throw new IllegalArgumentException("Trying to castle to a rank that is not the first or the eighth.");
                }
            }
            
            case EN_PASSANT -> {
                newSquares[fromFileIndex][fromRankIndex] = Square.valueOf(fromFileIndex, fromRankIndex, null);
                newSquares[toFileIndex][fromRankIndex] = Square.valueOf(toFileIndex, fromRankIndex, null);
                newSquares[toFileIndex][toRankIndex] = Square.valueOf(toFileIndex, toRankIndex, this.squares[fromFileIndex][fromRankIndex].getPiece());
            }
            case PROMOTION -> {
                newSquares[fromFileIndex][fromRankIndex] = Square.valueOf(fromFileIndex, fromRankIndex, null);
                newSquares[toFileIndex][toRankIndex] = Square.valueOf(toFileIndex, toRankIndex, move.getPromotionPiece());
            }
            default -> throw new IllegalStateException();
        }

        final boolean moveTypeIsNormalOrPromotion = moveType == MoveType.NORMAL || moveType == MoveType.PROMOTION;
        final boolean moveTypeIsCastling = moveType == MoveType.CASTLING;
        final boolean moveTypeIsEnPassant = moveType == MoveType.EN_PASSANT;
        final boolean didWhiteKingNotMove = !(this.toMove == Color.WHITE && movedPiece instanceof King);
        final boolean didBlackKingNotMove = !(this.toMove == Color.BLACK && movedPiece instanceof King);
        final boolean whiteToMove = this.toMove == Color.WHITE;
        final boolean blackToMove = this.toMove == Color.BLACK;

        return new Position(
            newSquares,
            new CastlingRights(
                this.castlingRights.whiteKingside()
                && (moveTypeIsNormalOrPromotion
                    && !(fromFileIndex == 7 && fromRankIndex == 0)
                    && !(toFileIndex == 7 && toRankIndex == 0)
                    && didWhiteKingNotMove
                || moveTypeIsCastling && blackToMove || moveTypeIsEnPassant),
                this.castlingRights.whiteQueenside()
                && (moveTypeIsNormalOrPromotion
                    && !(fromFileIndex == 0 && fromRankIndex == 0)
                    && !(toFileIndex == 0 && toRankIndex == 0)
                    && didWhiteKingNotMove
                || moveTypeIsCastling && blackToMove || moveTypeIsEnPassant),
                this.castlingRights.blackKingside()
                && (moveTypeIsNormalOrPromotion
                    && !(fromFileIndex == 7 && fromRankIndex == 7)
                    && !(toFileIndex == 7 && toRankIndex == 7)
                    && didBlackKingNotMove
                || moveTypeIsCastling && whiteToMove || moveTypeIsEnPassant),
                this.castlingRights.blackQueenside()
                && (moveTypeIsNormalOrPromotion
                    && !(fromFileIndex == 0 && fromRankIndex == 7)
                    && !(toFileIndex == 0 && toRankIndex == 7)
                    && didBlackKingNotMove
                || moveTypeIsCastling && whiteToMove || moveTypeIsEnPassant)),
            Moves.getEnPassantTarget(move, this),
            this.toMove == Color.WHITE
                ? Color.BLACK
                : Color.WHITE,
            Moves.isPawnMove(move, this) || Moves.isCapture(move, this)
                ? 0
                : this.halfMoveClock + 1,
            this.toMove == Color.BLACK
                ? this.fullMoves + 1
                : this.fullMoves);
    }


    @Override
    public String toString() {
        return this.generateFEN();
    }
    
    
}
