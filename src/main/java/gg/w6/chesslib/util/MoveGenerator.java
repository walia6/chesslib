package gg.w6.chesslib.util;

import java.util.HashSet;
import java.util.Set;

import gg.w6.chesslib.model.*;
import gg.w6.chesslib.model.piece.*;

public final class MoveGenerator {
    public static Set<Move> generatePseudoLegalMoves(final Position position) {
        final Set<Move> moves = new HashSet<>();
        final CastlingRights castlingRights = position.getCastlingRights();
        final Color toMove = position.getToMove();
    
        boolean canWhiteCastleKingside = castlingRights.whiteKingside();
        boolean canWhiteCastleQueenside = castlingRights.whiteQueenside();
        boolean canBlackCastleKingside = castlingRights.blackKingside();
        boolean canBlackCastleQueenside = castlingRights.blackQueenside();
    
        for (int fileIndex = 0; fileIndex < File.COUNT; fileIndex++) {
            for (int rankIndex = 0; rankIndex < Rank.COUNT; rankIndex++) {
                final Square square = position.getSquare(fileIndex, rankIndex);
                final Piece piece = square.getPiece();
                if (piece == null) continue;
    
                final Color pieceColor = piece.getColor();
                final Coordinate origin = Coordinate.valueOf(fileIndex, rankIndex);
                boolean castleCheck = false;
    
                if (pieceColor != toMove) {
                    if (piece instanceof Pawn
                            || toMove == Color.WHITE && !canWhiteCastleKingside && !canWhiteCastleQueenside
                            || toMove == Color.BLACK && !canBlackCastleKingside && !canBlackCastleQueenside) {
                        continue;
                    }
                    castleCheck = true;
                }
    
                if (piece instanceof final Rider rider) {
                    final int range = rider.getRange();
                    for (final Offset offset : rider.getOffsets()) {
                        for (final Coordinate target : offset.extendFrom(origin, range)) {
                            final Piece targetPiece = position.getSquare(target).getPiece();
    
                            if (!castleCheck) {
                                if (targetPiece == null || targetPiece.getColor() != toMove) {
                                    moves.add(new Move(origin, target, MoveType.NORMAL, null));
                                }
                            } else {
                                if (toMove == Color.WHITE) {
                                    if (canWhiteCastleKingside) {
                                        canWhiteCastleKingside = MoveGenerator.notOnCastlePath(target, 4, 0, 5, 0, 6, 0);
                                    }
                                    if (canWhiteCastleQueenside) {
                                        canWhiteCastleQueenside = MoveGenerator.notOnCastlePath(target, 4, 0, 3, 0, 2, 0);
                                    }
                                } else {
                                    if (canBlackCastleKingside) {
                                        canBlackCastleKingside = MoveGenerator.notOnCastlePath(target, 4, 7, 5, 7, 6, 7);
                                    }
                                    if (canBlackCastleQueenside) {
                                        canBlackCastleQueenside = MoveGenerator.notOnCastlePath(target, 4, 7, 3, 7, 2, 7);
                                    }
                                }
                            }
    
                            if (targetPiece != null) break;
                        }
                    }
                } else {
                    MoveGenerator.processPawnMoves(position, moves, toMove, fileIndex, rankIndex, origin);
                }
            }
        }
    
        MoveGenerator.checkPawnThreatsAndAddCastlingMoves(position, moves, canWhiteCastleKingside, canWhiteCastleQueenside,
                canBlackCastleKingside, canBlackCastleQueenside);
    
        return moves;
    }

    public static Set<Move> getLegalMoves(final Position position) {
        return MoveGenerator.generateLegalMoves(position);
    }

    private static void processPawnMoves(final Position position, final Set<Move> moves, final Color toMove,
                                         final int fileIndex, final int rankIndex, final Coordinate origin) {
        final int direction = toMove == Color.WHITE ? 1 : -1;
        final int startRank = toMove == Color.WHITE ? 1 : Rank.COUNT - 2;
    
        final Coordinate forwardOne = Coordinate.valueOf(fileIndex, rankIndex + direction);
        if (position.getSquare(forwardOne).getPiece() == null) {
            MoveGenerator.addPawnMovePromotionPossible(moves, toMove, origin, forwardOne);
            if (rankIndex == startRank) {
                final Coordinate forwardTwo = Coordinate.valueOf(fileIndex, rankIndex + 2 * direction);
                if (position.getSquare(forwardTwo).getPiece() == null) {
                    moves.add(new Move(origin, forwardTwo, MoveType.NORMAL, null));
                }
            }
        }
    
        for (int dx = -1; dx <= 1; dx += 2) {
            final int targetFile = fileIndex + dx;
            if (targetFile >= 0 && targetFile < File.COUNT) {
                final Coordinate target = Coordinate.valueOf(targetFile, rankIndex + direction);
                final Piece targetPiece = position.getSquare(target).getPiece();
                if (targetPiece != null && targetPiece.getColor() != toMove) {
                    MoveGenerator.addPawnMovePromotionPossible(moves, toMove, origin, target);
                }
            }
        }
    
        final Coordinate enPassantTarget = position.getEnPassantTarget();
        if (rankIndex == (toMove == Color.WHITE ? Rank.COUNT - 4 : 3) && enPassantTarget != null) {
            if (Math.abs(fileIndex - enPassantTarget.getFile().ordinal()) == 1) {
                moves.add(new Move(origin, enPassantTarget, MoveType.EN_PASSANT, null));
            }
        }
    }

    private static void addPawnMovePromotionPossible(final Set<Move> moves, final Color pawnColor,
            final Coordinate from, final Coordinate to) {
    
        if (to.getRank() == (pawnColor == Color.WHITE ? Rank.EIGHT : Rank.ONE)) {
            for (final Piece promotionPiece : Pieces.promotionCandidates.get(pawnColor)) {
                moves.add(new Move(from, to, MoveType.PROMOTION, promotionPiece));
            }
        } else {
            moves.add(new Move(from, to, MoveType.NORMAL, null));
        }
    }

    private static void checkPawnThreatsAndAddCastlingMoves(final Position position, final Set<Move> moves,
            final boolean canWhiteCastleKingside, final boolean canWhiteCastleQueenside,
            final boolean canBlackCastleKingside, final boolean canBlackCastleQueenside) {
    
        Move move;
    
        move = MoveGenerator.checkPawnThreatsForCastling(position, Color.WHITE, canWhiteCastleKingside,
                new int[] { 5, 6 }, new int[] { 3, 4, 5, 6 }, 0, 6); // White kingside
        if (move != null) moves.add(move);
    
        move = MoveGenerator.checkPawnThreatsForCastling(position, Color.WHITE, canWhiteCastleQueenside,
                new int[] { 1, 2, 3 }, new int[] { 2, 3, 4, 5 }, 0, 2); // White queenside
        if (move != null) moves.add(move);
    
        move = MoveGenerator.checkPawnThreatsForCastling(position, Color.BLACK, canBlackCastleKingside,
                new int[] { 5, 6 }, new int[] { 3, 4, 5, 6 }, 7, 6); // Black kingside
        if (move != null) moves.add(move);
    
        move = MoveGenerator.checkPawnThreatsForCastling(position, Color.BLACK, canBlackCastleQueenside,
                new int[] { 1, 2, 3 }, new int[] { 2, 3, 4, 5 }, 7, 2); // Black queenside
        if (move != null) moves.add(move);
    }

    private static Move checkPawnThreatsForCastling(final Position position, final Color color, final boolean canCastle,
                                                    final int[] emptyFileIndices, final int[] pawnCheckFiles, final int rank, final int targetFile) {
    
        if (!canCastle || position.getToMove() != color)
            return null;
    
        for (final int file : emptyFileIndices) {
            if (!position.getSquare(Coordinate.valueOf(file, rank)).isEmpty())
                return null;
        }
    
        final int pawnRank = rank + (color == Color.WHITE ? 1 : -1);
    
        for (final int file : pawnCheckFiles) {
            final Piece piece = position.getSquare(file, pawnRank).getPiece();
            if (piece != null && piece.getColor() != color && piece instanceof Pawn)
                return null;
        }
    
        return new Move(Coordinate.valueOf(4, rank), Coordinate.valueOf(targetFile, rank), MoveType.CASTLING, null);
    }

    private static boolean notOnCastlePath(final Coordinate target, final int... squares) {
        for (int i = 0; i < squares.length; i += 2) {
            if (target.equals(Coordinate.valueOf(squares[i], squares[i + 1]))) {
                return false;
            }
        }
        return true;
    }

    private static Set<Move> generateLegalMoves(final Position position) {
        final Set<Move> moves = new HashSet<>();
        for (final Move move : generatePseudoLegalMoves(position)) {
            final Position newPosition = position.applyTo(move);
            // find the notToMoveKing
            Coordinate notToMoveKingCoordinate = null;
            final Color toMove = newPosition.getToMove();
            final Color notToMove = toMove == Color.WHITE ? Color.BLACK : Color.WHITE;
            outerloop:
            for (int fileIndex = 0; fileIndex < File.COUNT; fileIndex++) {
                for (int rankIndex = 0; rankIndex < Rank.COUNT; rankIndex++) {
                    final Coordinate candidateCoordinate = Coordinate.valueOf(fileIndex, rankIndex);
                    final Piece candidate = newPosition.getSquare(candidateCoordinate).getPiece();
                    if (candidate instanceof final King king && king.getColor() == notToMove) {
                        notToMoveKingCoordinate = candidateCoordinate;
                        break outerloop;
                    }
                }
            }
            if (notToMoveKingCoordinate == null) {
                continue;
            }
            if (!PositionValidator.isTargetedByColor(notToMoveKingCoordinate, toMove, newPosition)) {
                moves.add(move);
            }
        }

        return moves;
    }

    private MoveGenerator() {
    } // ensure non-instantiability
}
