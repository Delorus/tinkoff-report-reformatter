package ru.sherb.tinkrep.parser.xlsx;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * @author maksim
 * @since 14.03.2020
 */
public class ParseRuleBuilder<T, L> {

    private final XLSXReaderBuilder<T, L> parent;
    private final boolean global;

    ParseRuleBuilder(XLSXReaderBuilder<T, L> parent) {
        this.parent = parent;
        global = false;
    }

    ParseRuleBuilder(XLSXReaderBuilder<T, L> parent, boolean global) {
        this.parent = parent;
        this.global = global;
    }

    public ParseRuleCheckBuilder<T, L> startWith(String prefix) {
        Objects.requireNonNull(prefix);
        return new ParseRuleCheckBuilder<>(parent, global, new PrefixChecker(prefix));
    }

    public ParseRuleCheckBuilder<T, L> regex(String prefix, String fullPattern) {
        Objects.requireNonNull(prefix);
        Objects.requireNonNull(fullPattern);
        return new ParseRuleCheckBuilder<>(parent, global, new RegexChecker(prefix, fullPattern));
    }

    public ParseRuleFromBuilder<T, L> from(String fromPrefix) {
        Objects.requireNonNull(fromPrefix);
        return new ParseRuleFromBuilder<>(parent, global, new PrefixChecker(fromPrefix));
    }

    public ParseRuleFromBuilder<T, L> from(String prefix, String fullPattern) {
        Objects.requireNonNull(prefix);
        Objects.requireNonNull(fullPattern);
        return new ParseRuleFromBuilder<>(parent, global, new RegexChecker(prefix, fullPattern));
    }

    //todo not extends?
    public static class ParseRuleCheckBuilder<T, L> {

        private final BiConsumer<T, L> EMPTY_CONSUMER = (t, s) -> {
        };

        private final XLSXReaderBuilder<T, L> parent;
        private final Checker checker;
        private final boolean global;

        private ParseRuleCheckBuilder(XLSXReaderBuilder<T, L> parent, boolean global, Checker checker) {
            this.parent = parent;
            this.global = global;
            this.checker = checker;
        }

        public XLSXReaderBuilder<T, L> result(BiConsumer<T, L> setter) {
            if (global) {
                parent.addGlobalRule(new ParseRuleOnSingleLine<>(checker, setter));
            } else {
                parent.addRule(new ParseRuleOnSingleLine<>(checker, setter));
            }
            return parent;
        }

        public XLSXReaderBuilder<T, L> ignore() {
            if (global) {
                parent.addGlobalRule(new ParseRuleOnSingleLine<>(checker, EMPTY_CONSUMER));
            } else {
                parent.addRule(new ParseRuleOnSingleLine<>(checker, EMPTY_CONSUMER));
            }
            return parent;
        }
    }

    public static class ParseRuleFromBuilder<T, L> {

        private final XLSXReaderBuilder<T, L> parent;
        private final boolean global;
        private final Checker from;

        private ParseRuleFromBuilder(XLSXReaderBuilder<T, L> parent, boolean global, Checker from) {
            this.parent = parent;
            this.global = global;
            this.from = from;
        }

        public ParseRuleFromToBuilder<T, L> to(String toPrefix) {
            return new ParseRuleFromToBuilder<>(parent, global, from, new PrefixChecker(toPrefix));
        }

        public ParseRuleFromToBuilder<T, L> to(String prefix, String regex) {
            return new ParseRuleFromToBuilder<>(parent, global, from, new RegexChecker(prefix, regex));
        }

        public XLSXReaderBuilder<T, L> result(BiConsumer<T, List<L>> setter) {
            if (global) {
                parent.addGlobalRule(new ParseRuleOnMultiLine<>(from, false, setter));
            } else {
                parent.addRule(new ParseRuleOnMultiLine<>(from, false, setter));
            }
            return parent;
        }
    }

    public static class ParseRuleFromToBuilder<T, L> {

        private final XLSXReaderBuilder<T, L> parent;
        private final boolean global;
        private final Checker from;
        private final Checker toChecker;

        private ParseRuleFromToBuilder(XLSXReaderBuilder<T, L> parent, boolean global, Checker from, Checker to) {
            this.parent = parent;
            this.global = global;
            this.from = from;
            this.toChecker = to;
        }

        public XLSXReaderBuilder<T, L> result(BiConsumer<T, List<L>> setter) {
            if (global) {
                parent.addGlobalRule(new ParseRuleOnMultiLine<>(from, false, toChecker, false, setter));
            } else {
                parent.addRule(new ParseRuleOnMultiLine<>(from, false, toChecker, false, setter));
            }
            return parent;
        }
    }
}
