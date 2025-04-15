package gg.w6.chesslib.util.jsonmappings.sanparser.testparse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpectedMove {
    public String uci;
    public String san;
    public String movetype;
}
