package gg.w6.chesslib.util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gg.w6.chesslib.model.Color;
import gg.w6.chesslib.model.PromotionCandidate;
import org.reflections.Reflections;

import gg.w6.chesslib.model.piece.Piece;

public class Pieces {

    private Pieces() {} // ensure non-instantiability

    public static final Map<Color, Set<Piece>> promotionCandidates;

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

        promotionCandidates = Map.ofEntries(
            Map.entry(Color.WHITE, Set.copyOf(whitePromotionPieces)),
            Map.entry(Color.BLACK, Set.copyOf(blackPromotionPieces)));
    }
}
