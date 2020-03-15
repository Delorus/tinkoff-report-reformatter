package ru.sherb.tinkrep.parser.xlsx;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author maksim
 * @since 14.03.2020
 */
public class XLSXReaderBuilder<T, L> {

    private final Sheet sheet;
    private final Supplier<T> entityGen;
    private final Function<List<String>, L> lineTransformer;

    private List<ParseRule<T, L>> rules = new ArrayList<>();
    private List<ParseRule<T, L>> globalRules = new ArrayList<>();

    public XLSXReaderBuilder(Sheet sheet, Supplier<T> generator, Function<List<String>, L> lineTransformer) {
        this.sheet = sheet;
        this.entityGen = generator;
        this.lineTransformer = lineTransformer;
    }

    public <N> XLSXReaderBuilder<N, L> readTo(Supplier<N> generator) {
        return new XLSXReaderBuilder<>(sheet, generator, lineTransformer);
    }

    public <N> XLSXReaderBuilder<T, N> transformLine(Function<List<String>, N> transformer) {
        return new XLSXReaderBuilder<>(sheet, entityGen, transformer);
    }

    public ParseRuleBuilder<T, L> newRule() {
        return new ParseRuleBuilder<>(this);
    }

    public ParseRuleBuilder<T, L> newGlobalRule() {
        return new ParseRuleBuilder<>(this, true);
    }

    public T read() {
        return new XLSXReader<>(sheet, entityGen, lineTransformer, rules, globalRules).read();
    }

    void addRule(ParseRule<T, L> rule) {
        rules.add(rule);
    }

    void addGlobalRule(ParseRule<T, L> rule) {
        globalRules.add(rule);
    }
}
