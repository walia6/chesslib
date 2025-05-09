package gg.w6.chesslib.model;

import gg.w6.chesslib.model.piece.King;
import gg.w6.chesslib.model.piece.Piece;
import gg.w6.chesslib.util.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.util.Iterator;

/**
 * Represents a chess position, including the arrangement of pieces on the
 * board, castling rights, active color, en passant target square, halfmove
 * clock, and fullmove number.
 */
@Immutable
public class Position implements Iterable<Square> {


    /**
     * Creates a {@code Position} object from a FEN string.
     * 
     * <p>This method is equivalent to {@link FenParser#parse(String)}</p>
     * @param fen the FEN string representing the position
     * @return a Position object representing the position
     * @throws IllegalArgumentException if the FEN string is malformed
     */
    @NotNull
    public static Position valueOf(@NotNull final String fen) {
        return FenParser.parse(fen);
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
     * <p>This method should <b>NOT</b> be used. Rather,
     * {@link #valueOf(String)} or {@link PositionBuilder} ought to be used.</p>
     *
     * @param squares          a <b>file index major</b> 2D array of
     *                         <code>Square</code> objects.
     * @param castlingRights   the castling rights of the position
     * @param enPassantTargets the target square for en passant captures, null
     *                         if there is no en passant target square
     * @param toMove           the color of the player who is to move next
     * @param halfMoveClock    the halfmove clock
     * @param fullMoves        the fullmove number
     */
    @ApiStatus.Internal
    public Position(final @NotNull Square[][] squares,
                    @NotNull final CastlingRights castlingRights,
                    @Nullable final Coordinate enPassantTargets,
                    @NotNull final Color toMove,
                    final int halfMoveClock,
                    final int fullMoves) {
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
    @NotNull
    public Square getSquare(@NotNull final File file,
                            @NotNull final Rank rank) {
        return squares[file.ordinal()][rank.ordinal()];
    }

    /**
     * Returns the square at the specified file and rank.
     * 
     * @param fileIndex  the index of the file
     * @param rankIndex  the index of the rank
     * @return the square at the specified file and rank
     * @throws ArrayIndexOutOfBoundsException if <code>fileIndex</code> or <code>rankIndex</code> are out of bounds
     */
    @NotNull
    public Square getSquare(final int fileIndex, final int rankIndex) {
        return squares[fileIndex][rankIndex];
    }

    /**
     * Returns the square at the specified coordinate.
     * 
     * @param coordinate the coordinate of the square
     * @return the square at the specified coordinate
     */
    @NotNull
    public Square getSquare(@NotNull final Coordinate coordinate) {
        return squares
                [coordinate.getFile().ordinal()]
                [coordinate.getRank().ordinal()];
    }

    /**
     * get the square at the given coordinate
     * @param coordinate a coordinate, as a {@link String}
     * @return the square at the given coordinate
     */
    public Square getSquare(final String coordinate) {
        return getSquare(Coordinate.valueOf(coordinate));
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

    /**
     * Applies the given move to this position and returns a new {@code Position}
     * representing the resulting board state.
     *
     * <p>This method handles all legal move types, including:</p>
     * <ul>
     *   <li>{@link MoveType#NORMAL} – A standard move from one square to another.</li>
     *   <li>{@link MoveType#CASTLING} – Kingside or queenside castling, including rook movement.</li>
     *   <li>{@link MoveType#EN_PASSANT} – En passant capture of a pawn.</li>
     *   <li>{@link MoveType#PROMOTION} – Promotion of a pawn to another piece.</li>
     * </ul>
     *
     * <p>Castling rights and en passant targets are updated accordingly. The halfmove
     * clock is reset if the move is a capture or a pawn move. The fullmove number is
     * incremented after a move by Black.</p>
     *
     * <p>If there is a Pawn double push, the En Passant Target square is set regardless of the legality of an En Passant</p>
     *
     * @param move the move to apply
     * @return a new {@code Position} reflecting the state of the board after applying the move
     * @throws IllegalArgumentException if castling is attempted with an invalid king destination
     */
    public Position applyTo(final Move move) {

        final Square[][] newSquares = this.squares.clone();

        for (int fileIndex = 0; fileIndex < File.COUNT; fileIndex++) {
            newSquares[fileIndex] = this.squares[fileIndex].clone();
        }

        final int fromFileIndex = move.getFrom().getFileIndex();
        final int fromRankIndex = move.getFrom().getRankIndex();
        final int toFileIndex = move.getTo().getFileIndex();
        final int toRankIndex = move.getTo().getRankIndex();

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
            
                newSquares[rookFromFile][toRankIndex] = Square.valueOf(rookFromFile, toRankIndex, null);
                newSquares[rookToFile][toRankIndex] = Square.valueOf(rookToFile, toRankIndex, this.squares[rookFromFile][toRankIndex].getPiece());
            
                if (toRankIndex != 0 && toRankIndex != 7) {
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
            Positions.getEnPassantTarget(this, move),
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


    @Override
    public @NotNull Iterator<Square> iterator() {
        return new Iterator<>() {
            private int fileIndex = 0;
            private int rankIndex = 0;

            @Override
            public boolean hasNext() {
                return fileIndex < File.COUNT && rankIndex < Rank.COUNT;
            }

            @Override
            public Square next() {
                final Square square = squares[fileIndex][rankIndex];

                rankIndex++;
                if (rankIndex == Rank.COUNT) {
                    rankIndex = 0;
                    fileIndex++;
                }

                return square;
            }
        };
    }
}
