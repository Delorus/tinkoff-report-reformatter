package ru.sherb.tinkrep.parser.xlsx;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author maksim
 * @since 14.03.2020
 */
public class RegexChecker implements Checker {

    private final Pattern prefix;
    private final Pattern pattern;

    public RegexChecker(String prefix, String pattern) {
        this.prefix = Pattern.compile(prefix);
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public boolean fastEquals(List<String> line) {
        if (line.isEmpty()) {
            return false;
        }

        return prefix.matcher(line.get(0)).find();
    }

    @Override
    public boolean fullEquals(List<String> line) {
        return pattern.matcher(String.join(" ", line)).matches();
    }
}
