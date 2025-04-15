package gg.w6.chesslib.util.jsonmappings.sanparser.testparse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCase {
    public StartPosition start;
    public List<ExpectedMove> expected;
}
