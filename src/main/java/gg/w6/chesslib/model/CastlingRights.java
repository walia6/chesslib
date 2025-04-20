package gg.w6.chesslib.model;

import javax.annotation.concurrent.Immutable;

/**
 * Represents the castling rights of both players in a chess game.
 */
@Immutable
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

    /**
     * @return <code>true</code> if white still has the right to castle kingside
     */
    public boolean whiteKingside() {
        return this.whiteKingside;
    }

    /**
     * @return <code>true</code> if white still has the right to castle queenside
     */
    public boolean whiteQueenside() {
        return this.whiteQueenside;
    }

    /**
     * @return <code>true</code> if black still has the right to castle kingside
     */
    public boolean blackKingside() {
        return this.blackKingside;
    }

    /**
     * @return <code>true</code> if black still has the right to castle queenside
     */
    public boolean blackQueenside() {
        return this.blackQueenside;
    }

    /**
     * Determine there are no castling rights.
     *
     * <p>Semantically equivalent to: <pre><code>
     *     whiteKingside()
     *     && whiteQueenside()
     *     && blackKingside()
     *     && blackQueenside()
     * </code></pre></p>
     * @return <code>true</code> if there are no castling rights.
     */
    public boolean allRightFalse() {
        return this.whiteKingside && this.whiteQueenside
                && this.blackKingside && this.blackQueenside;
    }

    /**
     * Determine if this is equivalent to another object.
     * @param obj the object with which to compare against
     * @return <code>true</code> if the other object is a
     * <code>CastlingRights</code> object and all rights are equivalent
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof final CastlingRights otherCastlingRights) {
            return otherCastlingRights.whiteKingside == whiteKingside
                    && otherCastlingRights.whiteQueenside == whiteQueenside
                    && otherCastlingRights.blackKingside == blackKingside
                    && otherCastlingRights.blackQueenside == blackQueenside;
        } else {
            return false;
        }
    }
}
