package ru.sherb.tinkrep.parser.xlsx;

import java.util.ArrayList;
import java.util.List;

/**
 * @author maksim
 * @since 14.03.2020
 */
interface ParseRule<T, L> {

    boolean fastEquals(List<String> cells);

    boolean fullEquals(List<String> cells);

    void handle(L line);

    void fill(T entity);
}
