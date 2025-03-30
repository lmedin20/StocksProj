package prices;

import exceptions.InvalidPriceException;

import java.util.Objects;

public class Price implements Comparable<Price>{
    private final int cents;

    public Price(int cents) {
        this.cents = cents;
    }

    public boolean isNegative() {
        return cents < 0;
    }

    public Price add(Price other) throws InvalidPriceException {
        if (other == null) {
            throw new InvalidPriceException("Cannot add a null Price object");
        }
        return new Price(this.cents + other.cents);
    }

    public Price subtract(Price other) throws InvalidPriceException {
        if (other == null) {
            throw new InvalidPriceException("Cannot subtract a null Price object");
        }
        return new Price(this.cents - other.cents);
    }

    public Price multiply(int multiplier) {
        return new Price(this.cents * multiplier);
    }

    public boolean greaterOrEqual(Price other) throws InvalidPriceException {
        if (other == null) {
            throw new InvalidPriceException("Cannot compare with a null Price object");
        }
        return this.cents >= other.cents;
    }

    public boolean lessOrEqual(Price other) throws InvalidPriceException {
        if (other == null) {
            throw new InvalidPriceException("Cannot compare with a null Price object");
        }
        return this.cents <= other.cents;
    }

    public boolean greaterThan(Price other) throws InvalidPriceException {
        if (other == null) {
            throw new InvalidPriceException("Cannot compare with a null Price object");
        }
        return this.cents > other.cents;
    }

    public boolean lessThan(Price other) throws InvalidPriceException {
        if (other == null) {
            throw new InvalidPriceException("Cannot compare with a null Price object");
        }
        return this.cents < other.cents;
    }
    public double toDouble() {
        return this.cents / 100.0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return cents == price.cents;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cents);
    }

    @Override
    public int compareTo(Price other) {
        if (other == null) {
            return -1;
        }
        return Integer.compare(this.cents, other.cents);
    }

    @Override
    public String toString() {
        return String.format("$%,.2f", cents / 100.0);
    }
}
