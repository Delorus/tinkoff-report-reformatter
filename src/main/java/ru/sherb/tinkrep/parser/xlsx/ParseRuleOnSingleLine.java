package ru.sherb.tinkrep.parser.xlsx;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author maksim
 * @since 14.03.2020
 */
public class ParseRuleOnSingleLine<T, L> implements ParseRule<T, L> {

    private final Checker checker;
    private final BiConsumer<T, L> setter;

    private List<L> lines = new ArrayList<>();

    public ParseRuleOnSingleLine(Checker checker, BiConsumer<T, L> setter) {
        this.checker = checker;
        this.setter = setter;
    }

    @Override
    public boolean fastEquals(List<String> line) {
        return checker.fastEquals(line);
    }

    @Override
    public boolean fullEquals(List<String> line) {
        return checker.fullEquals(line);
    }

    @Override
    public void handle(L line) {
        lines.add(line);
    }

    @Override
    public void fill(T entity) {
        for (L line : lines) {
            setter.accept(entity, line);
        }
    }
}
