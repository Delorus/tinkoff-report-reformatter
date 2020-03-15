package ru.sherb.tinkrep.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author maksim
 * @since 15.03.2020
 */
public class LocalDatePeriod {

    public static final LocalDatePeriod ZERO = new LocalDatePeriod(LocalDate.MIN, LocalDate.MIN);

    public static LocalDatePeriod parse(String period) {
        String[] parts = period.split("-");
        LocalDate start = formatter.parse(parts[0].trim(), LocalDate::from);
        LocalDate end = formatter.parse(parts[1].trim(), LocalDate::from);
        return new LocalDatePeriod(start, end);
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final LocalDate start;
    private final LocalDate end;

    public LocalDatePeriod(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalDatePeriod that = (LocalDatePeriod) o;
        return start.equals(that.start) &&
                end.equals(that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "(" + start + " - " + end + ')';
    }
}
