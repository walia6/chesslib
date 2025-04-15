package gg.w6.chesslib.util.jsonmappings.movegenerator.testgetlegalmoves;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCaseFile {
    public List<TestCase> testCases;
}