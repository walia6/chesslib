package gg.w6.chesslib.util;


import gg.w6.chesslib.model.*;
import gg.w6.chesslib.model.piece.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SanParser {
    private static final Pattern CASTLING_SHORT = Pattern.compile("O-O");
    private static final Pattern CASTLING_LONG = Pattern.compile("O-O-O");
    private static final Pattern PAWN_PROMOTION = Pattern.compile("^([a-h])?(x)?([a-h][18])=([NBRQ])$");
    private static final Pattern PAWN_MOVE = Pattern.compile("^([a-h])?(x)?([a-h][2-7])$");
    private static final Pattern PIECE_MOVE = Pattern.compile("^([NBRQK])([a-h1-8]?)(x)?([a-h][1-8])$");

    public static Move parse(final String san, final Position position) {
        final String cleanSan = san.replace("+", "").replace("#", "");

        if (CASTLING_SHORT.matcher(cleanSan).matches()) {
            return parseCastling(position, true);
        } else if (CASTLING_LONG.matcher(cleanSan).matches()) {
            return parseCastling(position, false);
        }

        final Matcher promo = PAWN_PROMOTION.matcher(cleanSan);
        if (promo.matches()) {
            return parsePawnPromotion(promo, position);
        }

        final Matcher pawn = PAWN_MOVE.matcher(cleanSan);
        if (pawn.matches()) {
            return parsePawnMove(pawn, position);
        }

        final Matcher piece = PIECE_MOVE.matcher(cleanSan);
        if (piece.matches()) {
            return parsePieceMove(piece, position, san);
        }

        throw new IllegalArgumentException("Unrecognized SAN: " + san + ". fen=" + position.generateFEN());
    }

    private static Move parseCastling(Position position, boolean kingside) {
        final Coordinate from = position.getToMove() == Color.WHITE ? Coordinate.valueOf("e1") : Coordinate.valueOf("e8");
        final Coordinate to = position.getToMove() == Color.WHITE ?
                (kingside ? Coordinate.valueOf("g1") : Coordinate.valueOf("c1")) :
                (kingside ? Coordinate.valueOf("g8") : Coordinate.valueOf("c8"));
        return new Move(from, to, MoveType.CASTLING, null);
    }

    private static Move parsePawnPromotion(Matcher matcher, Position position) {
        String fromFile = matcher.group(1);
        boolean isCapture = matcher.group(2) != null;
        String toStr = matcher.group(3);
        String promoPiece = matcher.group(4);

        Coordinate to = Coordinate.valueOf(toStr);
        int toRank = to.getRankIndex();
        int fromRank = position.getToMove() == Color.WHITE ? toRank - 1 : toRank + 1;

        Coordinate from = null;
        if (isCapture) {
            int fileIdx = to.getFileIndex();
            int offset = fromFile.charAt(0) - 'a';
            from = Coordinate.valueOf(offset, fromRank);
        } else {
            from = Coordinate.valueOf(to.getFileIndex(), fromRank);
        }

        Piece promotionPiece = createPieceFromSymbol(promoPiece, position.getToMove());
        return new Move(from, to, MoveType.PROMOTION, promotionPiece);
    }

    private static Move parsePawnMove(Matcher matcher, Position position) {
        String fromFile = matcher.group(1);
        boolean isCapture = matcher.group(2) != null;
        String toStr = matcher.group(3);

        Coordinate to = Coordinate.valueOf(toStr);
        int toRank = to.getRankIndex();
        int fromRank = position.getToMove() == Color.WHITE ? toRank - 1 : toRank + 1;

        Coordinate from = null;
        if (isCapture) {
            int fileIdx = fromFile.charAt(0) - 'a';
            from = Coordinate.valueOf(fileIdx, fromRank);

            if (position.getSquare(to).getPiece() == null &&
                    to.equals(position.getEnPassantTarget())) {
                return new Move(from, to, MoveType.EN_PASSANT, null);
            }
        } else {
            from = Coordinate.valueOf(to.getFileIndex(), fromRank);
            if (position.getSquare(from).getPiece() == null) {
                fromRank = position.getToMove() == Color.WHITE ? toRank - 2 : toRank + 2;
                from = Coordinate.valueOf(to.getFileIndex(), fromRank);
            }
        }

        return new Move(from, to, MoveType.NORMAL, null);
    }

    private static Move parsePieceMove(Matcher matcher, Position position, String san) {
        String symbol = matcher.group(1);
        String disambiguation = matcher.group(2);
        String toStr = matcher.group(4);

        Coordinate to = Coordinate.valueOf(toStr);
        Color activeColor = position.getToMove();
        List<Coordinate> candidates = new ArrayList<>();

        for (Square square : position) {
            Piece p = square.getPiece();
            if (p == null) continue;
            if (!p.getColor().equals(activeColor)) continue;
            if (Character.toUpperCase(p.getLetter()) != symbol.charAt(0)) continue;


            if (p instanceof Rider) {
                Rider rider = (Rider) p;
                for (Offset offset : rider.getOffsets()) {
                    for (Coordinate coord : offset.extendFrom(square.getCoordinate(), rider.getRange())) {
                        if (coord.equals(to)) {
                            candidates.add(square.getCoordinate());
                            break;
                        }
                        if (position.getSquare(coord).getPiece() != null) break;
                    }
                }
            } else if (p instanceof Knight || p instanceof King) {
                Rider rider = (Rider) p;
                for (Offset offset : rider.getOffsets()) {
                    for (Coordinate coord : offset.extendFrom(square.getCoordinate(), 1)) {
                        if (coord.equals(to)) {
                            candidates.add(square.getCoordinate());
                        }
                    }
                }
            }
        }

        Coordinate from = disambiguate(candidates, disambiguation, position, to, san);
        return new Move(from, to, MoveType.NORMAL, null);
    }

    private static Coordinate disambiguate(List<Coordinate> candidates, String disambiguation, Position position, Coordinate to, String san) {
        if (candidates.size() == 1) return candidates.get(0);

        for (Coordinate c : candidates) {
            if (disambiguation.length() == 1) {
                char ch = disambiguation.charAt(0);
                if (ch >= 'a' && ch <= 'h' && c.getFile().name().toLowerCase().charAt(0) == ch) {
                    return c;
                } else if (ch >= '1' && ch <= '8') {
                    int disambiguationRankIndex = ch - '1';
                    if (c.getRankIndex() == disambiguationRankIndex) {
                        return c;
                    }
                }
            } else if (disambiguation.length() == 2) {
                Coordinate d = Coordinate.valueOf(disambiguation);
                if (c.equals(d)) return c;
            }
        }

        final List<Coordinate> legalCandidateCoordinates = new ArrayList<>();

        for (Coordinate c : candidates) {
            final Move candidateMove = new Move(c, to, MoveType.NORMAL, null);
            final Position newPosition = position.applyTo(candidateMove);
            final PositionValidator.Legality legality = PositionValidator.getLegality(newPosition);
            if (legality != PositionValidator.Legality.CAN_CAPTURE_KING) {
                legalCandidateCoordinates.add(c);
            }
        }

        if (legalCandidateCoordinates.size() == 1) {
            return legalCandidateCoordinates.get(0);
        }

        throw new IllegalArgumentException("Ambiguous SAN: " + candidates + " with disambiguation: " + disambiguation +  ". san=\"" + san + "\" fen=" + position.generateFEN());
    }

    private static Piece createPieceFromSymbol(String symbol, Color color) {
        switch (symbol) {
            case "Q": return new Queen(color);
            case "R": return new Rook(color);
            case "B": return new Bishop(color);
            case "N": return new Knight(color);
            default: throw new IllegalArgumentException("Unknown promotion symbol: " + symbol);
        }
    }

}
