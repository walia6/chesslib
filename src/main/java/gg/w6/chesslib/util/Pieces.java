package gg.w6.chesslib.util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gg.w6.chesslib.model.Color;
import gg.w6.chesslib.model.PromotionCandidate;
import org.reflections.Reflections;

import gg.w6.chesslib.model.piece.Piece;

/**
 * This class consists of a single static member, {@link #PROMOTION_CANDIDATES}.
 */
public class Pieces {

    private Pieces() {} // ensure non-instantiability

    /**
     * This is a mapping of the {@link Color}s to {@link Piece}s.
     *
     * <p>All pieces that may be promoted to are values of this map.</p>
     *
     * <p>This field is created statically at runtime by finding all classes of
     * {@link gg.w6.chesslib.model.piece} that are annotated with
     * {@link PromotionCandidate}.</p>
     *
     * <p>This field is immutable.</p>
     *
     * <p>Usage example:</p>
     * <pre><code>
     *     if (to.getRank() == (pawnColor == Color.WHITE ? Rank.EIGHT : Rank.ONE)) {
     *         for (final Piece promotionPiece : Pieces.PROMOTION_CANDIDATES.get(pawnColor)) {
     *             moves.add(new Move(from, to, MoveType.PROMOTION, promotionPiece));
     *         }
     *     }
     * </code></pre>
     */
    public static final Map<Color, Set<Piece>> PROMOTION_CANDIDATES;

    static {

        final Set<Piece> whitePromotionPieces = new HashSet<>();
        final Set<Piece> blackPromotionPieces = new HashSet<>();
        Reflections reflections = new Reflections("gg.w6.chesslib.model.piece");

        for (Class<?> annotatedClass : reflections.getTypesAnnotatedWith(
                    PromotionCandidate.class)) {
            if (!Piece.class.isAssignableFrom(annotatedClass)
                    || annotatedClass == Piece.class) {
                throw new IllegalArgumentException(annotatedClass.getName()
                        + " is annotated with @PromotionCandidate but is not a"
                        + " subclass of Piece."); 
            }

            @SuppressWarnings("unchecked")
            Class<? extends Piece> pieceClass =
                    (Class<? extends Piece>) annotatedClass;

            try {
                whitePromotionPieces.add(pieceClass.getDeclaredConstructor(
                        Color.class).newInstance(Color.WHITE));
                blackPromotionPieces.add(pieceClass.getDeclaredConstructor(
                        Color.class).newInstance(Color.BLACK));
            } catch (InstantiationException | IllegalAccessException
                    | InvocationTargetException | NoSuchMethodException e) {
                throw new IllegalStateException(
                            "Failed to instantiate promotion piece: "
                            + pieceClass.getName(), e);
            }
        }

        PROMOTION_CANDIDATES = Map.ofEntries(
            Map.entry(Color.WHITE, Set.copyOf(whitePromotionPieces)),
            Map.entry(Color.BLACK, Set.copyOf(blackPromotionPieces)));
    }
}
