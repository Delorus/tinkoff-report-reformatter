package ru.sherb.tinkrep.parser;

import org.javamoney.moneta.Money;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author maksim
 * @since 15.03.2020
 */
public final class ParseUtils {

    private ParseUtils() {
        throw new IllegalStateException();
    }

    public static LocalDate parseLocalDate(String date) {
        Objects.requireNonNull(date);
        if (date.isBlank()) {
            throw new IllegalArgumentException("Date must not be blank");
        }
        return DateTimeFormatter.ofPattern("dd.MM.yyyy").parse(date, LocalDate::from);
    }

    public static Money parseMoney(String value, String currency) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(currency);
        if (value.isBlank()) {
            throw new IllegalArgumentException("Value must not be blank");
        }
        if (currency.isBlank()) {
            throw new IllegalArgumentException("Currency must not be blank");
        }
        value = value.replace(",", ".");

        return Money.of(new BigDecimal(value), currency);
    }
}
