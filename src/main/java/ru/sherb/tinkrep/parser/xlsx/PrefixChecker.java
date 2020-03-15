package ru.sherb.tinkrep.parser.xlsx;

import java.util.List;

/**
 * @author maksim
 * @since 14.03.2020
 */
public class PrefixChecker implements Checker {

    private final String prefix;

    public PrefixChecker(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean fastEquals(List<String> line) {
        if (line.isEmpty()) {
            return false;
        }

        return line.get(0).startsWith(prefix.substring(0, 3));
    }

    @Override
    public boolean fullEquals(List<String> line) {
        return String.join(" ", line).startsWith(prefix);
    }
}
