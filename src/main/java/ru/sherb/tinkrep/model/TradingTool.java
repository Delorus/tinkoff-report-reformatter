package ru.sherb.tinkrep.model;

import java.util.Objects;

/**
 * @author maksim
 * @since 15.03.2020
 */
public class TradingTool {

    private final String name;
    private final String id;

    public TradingTool(String name, String id) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(id);
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank: " + name);
        }
        if (id.isBlank()) {
            throw new IllegalArgumentException("Id must not be blank: " + id);
        }

        this.name = name;
        this.id = id;
    }
}
