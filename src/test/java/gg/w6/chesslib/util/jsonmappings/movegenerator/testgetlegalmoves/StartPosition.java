package gg.w6.chesslib.util.jsonmappings.movegenerator.testgetlegalmoves;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StartPosition {
    public String fen;
}