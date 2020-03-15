package ru.sherb.tinkrep.parser.xlsx;

import java.util.List;

/**
 * @author maksim
 * @since 14.03.2020
 */
interface Checker {

    Checker ALWAYS_FALSE = new Checker() {
        @Override
        public boolean fastEquals(List<String> line) {
            return false;
        }

        @Override
        public boolean fullEquals(List<String> line) {
            return false;
        }
    };

    boolean fastEquals(List<String> line);

    boolean fullEquals(List<String> line);

}
