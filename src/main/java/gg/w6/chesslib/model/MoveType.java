package gg.w6.chesslib.model;

import javax.annotation.concurrent.Immutable;

/**
 * Represents the various types of moves in a position
 */
@Immutable
public enum MoveType {
    /**
     * Represents a normal move, capturing or not
     */
    NORMAL,

    /**
     * Represents a castling move
     */
    CASTLING,

    /**
     * Represents an EnPassant move
     */
    EN_PASSANT,

    /**
     * Represents a promotion, capturing or not
     */
    PROMOTION
}
