package ru.sherb.tinkrep.model;

import lombok.Value;

import java.util.Objects;

/**
 * @author maksim
 * @since 15.03.2020
 */
@Value
public class TradingTool {

    String name;
    String code;

    public TradingTool(String name, String code) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(code);
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank: " + name);
        }
        if (code.isBlank()) {
            throw new IllegalArgumentException("Id must not be blank: " + code);
        }

        this.name = name;
        this.code = code;
    }
}
