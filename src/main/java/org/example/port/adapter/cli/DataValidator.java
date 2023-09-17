package com.example.treasuremap.port.adapter.cli;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataValidator {


    private static final String MAP_LINE_PATTERN = "(^C - \\d+ - \\d+$)";
    private static final String MOUNTAIN_PATTERN = "(^M - \\d+ - \\d+$)";
    private static final String TREASURE_PATTERN = "(^T - \\d+ - \\d+ - \\d+$)";
    private static final String ADVENTURER_PATTERN = "(^A - ([a-zA-Z]+\\d*) - \\d+ - \\d+ - (N|E|W|S) - [A|D|G]*$)";
    private static final String COMMENT_PATTERN = "(^#.*$)";

    public boolean isValid(String data) {
        Pattern commandRegex = Pattern.compile(
                        MAP_LINE_PATTERN
                        + "|" + MOUNTAIN_PATTERN
                        + "|" + TREASURE_PATTERN
                        + "|" + ADVENTURER_PATTERN
                        + "|" + COMMENT_PATTERN);
        Matcher matcher = commandRegex.matcher(data);
        return matcher.find();
    }
}
