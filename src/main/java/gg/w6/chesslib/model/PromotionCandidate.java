package gg.w6.chesslib.model;

import gg.w6.chesslib.model.piece.Piece;
import gg.w6.chesslib.util.Pieces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Any classes with this annotation are put on the list of possible promotion
 * pieces. This is generated at runtime.
 *
 * <p>Only subclasses of {@link Piece} should have this
 * annotation.</p>
 * <p>See {@link Pieces#PROMOTION_CANDIDATES}.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PromotionCandidate {}
