package gg.w6.util;

/**
 * Represents the castling rights of both players in a chess game.
 *
 * <p>Castling is a special move in chess that involves the King and a Rook.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * CastlingRights rights = new CastlingRights();
 * rights.setWhiteKingside(false);
 * }</pre>
 */
public class CastlingRights {
    
    /**
     * The castling rights for the white player.
     * <p>True if the player can castle kingside, false otherwise.</p>
     */
    private final boolean whiteKingside;

    /**
     * The castling rights for the white player.
     * <p>True if the player can castle queenside, false otherwise.</p>
     */
    private final boolean whiteQueenside;

    /**
     * The castling rights for the black player.
     * <p>True if the player can castle kingside, false otherwise.</p>
     */
    private final boolean blackKingside;

    /**
     * The castling rights for the black player.
     * <p>True if the player can castle queenside, false otherwise.</p>
     */
    private final boolean blackQueenside;

    /**
     * Constructs a new CastlingRights object with all rights enabled.
     */
    public CastlingRights() {
        this(true);
    }

    /**
     * Constructs a new CastlingRights object with the specified rights.
     *
     * @param allRights True if all castling rights are enabled, false otherwise.
     */
    public CastlingRights(final boolean allRights) {
        this.whiteKingside = allRights;
        this.whiteQueenside = allRights;
        this.blackKingside = allRights;
        this.blackQueenside = allRights;
    }

    /**
     * Constructs a new CastlingRights object with the specified rights.
     *
     * @param whiteKingside   True if the white player can castle kingside, false otherwise.
     * @param whiteQueenside  True if the white player can castle queenside, false otherwise.
     * @param blackKingside   True if the black player can castle kingside, false otherwise.
     * @param blackQueenside  True if the black player can castle queenside, false otherwise.
     */
    public CastlingRights(final boolean whiteKingside,
                final boolean whiteQueenside, final boolean blackKingside,
                final boolean blackQueenside) {
        this.whiteKingside = whiteKingside;
        this.whiteQueenside = whiteQueenside;
        this.blackKingside = blackKingside;
        this.blackQueenside = blackQueenside;
    }

    public boolean whiteKingside() {
        return this.whiteKingside;
    }

    public boolean whiteQueenside() {
        return this.whiteQueenside;
    }

    public boolean blackKingside() {
        return this.blackKingside;
    }

    public boolean blackQueenside() {
        return this.blackQueenside;
    }

    public boolean allRightFalse() {
        return this.whiteKingside && this.whiteQueenside
                && this.blackKingside && this.blackQueenside;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CastlingRights otherCastlingRights) {
            return otherCastlingRights.whiteKingside == whiteKingside
                    && otherCastlingRights.whiteQueenside == whiteQueenside
                    && otherCastlingRights.blackKingside == blackKingside
                    && otherCastlingRights.blackQueenside == blackQueenside;
        } else {
            return false;
        }
    }

}
