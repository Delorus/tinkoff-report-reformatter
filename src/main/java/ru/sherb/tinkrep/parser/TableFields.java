package ru.sherb.tinkrep.parser;

/**
 * @author maksim
 * @since 15.03.2020
 */
@FunctionalInterface
public interface TableFields {

    TableField getField(String title);
}
