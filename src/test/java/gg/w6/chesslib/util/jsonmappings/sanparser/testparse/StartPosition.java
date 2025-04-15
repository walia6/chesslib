package gg.w6.chesslib.util.jsonmappings.sanparser.testparse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StartPosition {
    public String fen;
}
