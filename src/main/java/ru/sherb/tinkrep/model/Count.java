package ru.sherb.tinkrep.model;

import java.util.Objects;

/**
 * @author maksim
 * @since 15.03.2020
 */
public final class Count extends Number {
    private final int count;

    public Count(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Must be greater than zero: " + count);
        }
        this.count = count;
    }

    @Override
    public int intValue() {
        return count;
    }

    @Override
    public long longValue() {
        return count;
    }

    @Override
    public float floatValue() {
        return count;
    }

    @Override
    public double doubleValue() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Count count1 = (Count) o;
        return count == count1.count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count);
    }

    @Override
    public String toString() {
        return Integer.toString(count);
    }
}
