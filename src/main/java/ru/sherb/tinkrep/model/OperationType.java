package ru.sherb.tinkrep.model;

import java.util.Objects;

/**
 * @author maksim
 * @since 15.03.2020
 */
public enum OperationType {
    BUY, SELL;

    public static OperationType parse(String raw) {
        Objects.requireNonNull(raw);
        if (raw.isBlank()) {
            throw new IllegalArgumentException("Operation type must not be blank");
        }

        if (raw.contains("Продажа")) {
            return SELL;
            //todo РЕПО?
        } else if (raw.contains("Покупка")) {
            return BUY;
        }

        throw new IllegalArgumentException("Incorrect value of operation type: " + raw);
    }
}
