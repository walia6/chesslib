package gg.w6.chesslib.util;

import gg.w6.chesslib.model.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PgnParser {

    private PgnParser() {
    } // ensure non-instantiability

    private static final Pattern TAG_PAIR_PATTERN = Pattern.compile("\\[(\\w+)\\s+\"([^\"]*)\"]");
    private static final Pattern MOVE_TEXT_PATTERN = Pattern.compile("(?s)\\s*(?:\\[.*])+\\s*(.*?)\\s*(1-0|0-1|1/2-1/2|\\*)");

    public static Game parse(final String rawPgn) {
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
            String tag = tagMatcher.group(1);
            String value = tagMatcher.group(2);
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
        String moveSection = rawPgn.substring(lastTagEnd).trim();

        // Remove comments, NAGs, variations, and normalize spacing
        String cleaned = moveSection
                .replaceAll("\\{[^}]*}", " ")               // remove comments
                .replaceAll("\\$\\d+", " ")                 // remove NAGs
                .replaceAll("\\([^)]*\\)", " ")             // remove variations
                .replaceAll("\\d+\\.\\.\\.|\\d+\\.", " ")   // remove move numbers (1. and 1...)
                .replaceAll("\\s+", " ")                    // normalize spacing
                .trim();


        // Remove trailing result token from SAN list if present
        List<String> sanStrings = new ArrayList<>(Arrays.asList(cleaned.split(" ")));
        if (!sanStrings.isEmpty()) {
            String last = sanStrings.get(sanStrings.size() - 1);
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
