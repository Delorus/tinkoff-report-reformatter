package ru.sherb.tinkrep.parser.xlsx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author maksim
 * @since 15.03.2020
 */
public class ParseRuleOnMultiLine<T, L> implements ParseRule<T, L> {

    private final Checker from;
    private final boolean fromInclude;
    private final Checker to;
    private final boolean toInclude;
    private final BiConsumer<T, List<L>> setter;

    private final List<List<L>> lines = new ArrayList<>();
    private State state = State.OUT_RANGE;
    private List<L> currentRangeLines = Collections.emptyList();

    ParseRuleOnMultiLine(Checker from, boolean fromInclude, BiConsumer<T, List<L>> setter) {
        this(from, fromInclude, Checker.ALWAYS_FALSE, false, setter);
    }

    ParseRuleOnMultiLine(Checker from, boolean fromInclude, Checker to, boolean toInclude, BiConsumer<T, List<L>> setter) {
        this.from = from;
        this.fromInclude = fromInclude;
        this.to = to;
        this.toInclude = toInclude;
        this.setter = setter;
    }

    @Override
    public boolean fastEquals(List<String> cells) {
        if (cells.isEmpty()) {
            return false;
        }

        switch (state) {
            case OUT_RANGE:
                return from.fastEquals(cells);
            case FIRST:
                return true;
            case IN_RANGE:
                return true;
            case LAST:
                return false;
        }

        return false;
    }

    @Override
    public boolean fullEquals(List<String> cells) {
        if (cells.isEmpty()) {
            return false;
        }

        switch (state) {
            case OUT_RANGE:
                boolean firstLine = from.fullEquals(cells);
                if (firstLine) {
                    state = State.FIRST;
                }
                return firstLine;
            case FIRST:
                boolean lastLine = to.fullEquals(cells);
                if (lastLine) {
                    state = State.LAST;
                } else {
                    state = State.IN_RANGE;
                }
                return true;
            case IN_RANGE:
                lastLine = to.fullEquals(cells);
                if (lastLine) {
                    state = State.LAST;
                }
                return true;
            case LAST:
                return false;
        }

        return false;
    }

    @Override
    public void handle(L line) {
        switch (state) {
            case FIRST:
                currentRangeLines = new ArrayList<>();
                if (fromInclude) {
                    currentRangeLines.add(line);
                }
                lines.add(currentRangeLines);
                break;
            case IN_RANGE:
                currentRangeLines.add(line);
                break;
            case LAST:
                if (toInclude) {
                    currentRangeLines.add(line);
                }
                currentRangeLines = Collections.emptyList();
                break;
        }
    }

    @Override
    public void fill(T entity) {
        for (List<L> range : lines) {
            setter.accept(entity, range);
        }
    }

    private enum State {
        OUT_RANGE, FIRST, IN_RANGE, LAST
    }
}
