package gg.w6.chesslib.util;

import gg.w6.chesslib.model.Game;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a static utility class for parsing PGN strings representing a
 * single game of Chess. It is not instantiable.
 *
 * <p>To parse several PGN strings contained within a file, one should use {@link PgnDatabaseSplitter}</p>
 *
 * <p>The only public member of this class is {@link #parse(String)}.</p>
 */
public class PgnParser {

    private PgnParser() {
    } // ensure non-instantiability

    private static final Pattern TAG_PAIR_PATTERN = Pattern.compile("\\[(\\w+)\\s+\"([^\"]*)\"]");
    private static final Pattern COMMENT_PATTERN = Pattern.compile("\\{[^}]*}");
    private static final Pattern NAG_PATTERN = Pattern.compile("\\$\\d+");
    private static final Pattern VARIATION_PATTERN = Pattern.compile("\\([^)]*\\)");
    private static final Pattern MOVE_NUMBER_PATTERN = Pattern.compile("\\d+\\.\\.\\.|\\d+\\.");
    private static final Pattern MULTISPACE_PATTERN = Pattern.compile("\\s+");

    /**
     * Parse a PGN string containing a single game and return a {@link Game} record encapsulating said game.
     *
     * <p>This method is <i>not</i> to be used to parse multiple PGNs at once. For such, one should use {@link PgnDatabaseSplitter}</p>
     *
     * <p>Usage example:</p>
     * <pre><code>
     * try (PgnDatabaseSplitter splitter = new PgnDatabaseSplitter(new File("games.pgn"))) {
     *     for (String pgnString : splitter) {
     *         Game game = PgnParser.parse(pgnString);
     *     }
     * }
     * </code></pre>
     *
     * @param rawPgn the PGN to parse
     * @return a {@link Game} encapsulating the parsed PGN
     */
    @NotNull
    public static Game parse(@NotNull final String rawPgn) {
        String event = null;
        String site = null;
        String date = null;
        String round = null;
        String white = null;
        String black = null;
        String result = null;
        String whiteElo = null;
        String blackElo = null;
        String eco = null;
        String opening = null;
        String timeControl = null;
        String termination = null;
        String annotator = null;

        Matcher tagMatcher = TAG_PAIR_PATTERN.matcher(rawPgn);
        int lastTagEnd = 0;

        while (tagMatcher.find()) {
            final String tag = tagMatcher.group(1);
            final String value = tagMatcher.group(2);
            lastTagEnd = tagMatcher.end(); // update end of last tag match

            switch (tag) {
                case "Event" -> event = value;
                case "Site" -> site = value;
                case "Date" -> date = value;
                case "Round" -> round = value;
                case "White" -> white = value;
                case "Black" -> black = value;
                case "Result" -> result = value;
                case "WhiteElo" -> whiteElo = value;
                case "BlackElo" -> blackElo = value;
                case "ECO" -> eco = value;
                case "Opening" -> opening = value;
                case "TimeControl" -> timeControl = value;
                case "Termination" -> termination = value;
                case "Annotator" -> annotator = value;
            }
        }

        // Get the movetext section (after the last tag)

        // Remove comments, NAGs, variations, and normalize spacing
        String cleaned = rawPgn.substring(lastTagEnd).trim();
        cleaned = COMMENT_PATTERN.matcher(cleaned).replaceAll(" ");
        cleaned = NAG_PATTERN.matcher(cleaned).replaceAll(" ");
        cleaned = VARIATION_PATTERN.matcher(cleaned).replaceAll(" ");
        cleaned = MOVE_NUMBER_PATTERN.matcher(cleaned).replaceAll(" ");
        cleaned = MULTISPACE_PATTERN.matcher(cleaned).replaceAll(" ").trim();



        // Remove trailing result token from SAN list if present
        final List<String> sanStrings = new ArrayList<>(Arrays.asList(cleaned.split(" ")));
        if (!sanStrings.isEmpty()) {
            final String last = sanStrings.get(sanStrings.size() - 1);
            if (last.equals("1-0") || last.equals("0-1") || last.equals("1/2-1/2") || last.equals("*")) {
                result = last;
                sanStrings.remove(sanStrings.size() - 1);
            }
        }

        return new Game(
                rawPgn, sanStrings, annotator, termination, timeControl, opening, eco,
                blackElo, whiteElo, result, black, white, round, date, site, event
        );
    }

}
