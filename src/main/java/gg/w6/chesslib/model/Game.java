package gg.w6.chesslib.model;

import java.util.List;

/**
 * @param event       From [Event "..."]
 * @param site        [Site "..."]
 * @param date        [Date "..."]
 * @param round       [Round "..."]
 * @param white       [White "..."]
 * @param black       [Black "..."]
 * @param result      [Result "1-0", "0-1", "1/2-1/2", "*"]
 * @param whiteElo    [WhiteElo "..."] (optional)
 * @param blackElo    [BlackElo "..."]
 * @param eco         [ECO "..."] (e.g. C42)
 * @param opening     [Opening "..."] (optional)
 * @param timeControl [TimeControl "..."]
 * @param termination [Termination "..."]
 * @param annotator   [Annotator "..."]
 */
public record Game(String rawPgn, List<String> sanStrings, String annotator,
                   String termination, String timeControl, String opening,
                   String eco, String blackElo, String whiteElo, String result,
                   String black, String white, String round, String date,
                   String site, String event) {

}
