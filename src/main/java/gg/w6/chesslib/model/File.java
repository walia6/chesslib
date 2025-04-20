package gg.w6.chesslib.model;

import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;

/**
 * Represents the files on a chessboard.
 *
 * <p>Files are the vertical columns on a chessboard, labeled from 'a' to 'h'.</p>
 */
@Immutable
public enum File {

    /**
     * Represents the 'a' file.
     */
    A,

    /**
     * Represents the 'b' file.
     */
    B,

    /**
     * Represents the 'c' file.
     */
    C,

    /**
     * Represents the 'd' file.
     */
    D,

    /**
     * Represents the 'e' file.
     */
    E,

    /**
     * Represents the 'f' file.
     */
    F,

    /**
     * Represents the 'g' file.
     */
    G,

    /**
     * Represents the 'h' file.
     */
    H;

    /**
     * The number of files on a chessboard.
     */
    public static final int COUNT = 8;

    /**
     * Returns the character representation of the file.
     */
    @Override
    @NotNull
    public String toString() {
        return this.name().toLowerCase();
    }
}
