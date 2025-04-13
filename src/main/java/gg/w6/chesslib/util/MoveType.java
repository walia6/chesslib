package gg.w6.chesslib.util;

/**
 * Represents the various types of moves in a position
 */
public enum MoveType {
    /**
     * Represents a normal move, capturing or not
     */
    NORMAL,

    /**
     * Represents any castling move
     */
    CASTLING,

    /**
     * Represents any enpassant move
     */
    EN_PASSANT,

    /**
     * Represents any promotion
     */
    PROMOTION
}
