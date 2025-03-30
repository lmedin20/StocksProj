package prices;

import exceptions.InvalidPriceException;

import java.util.HashMap;
import java.util.Map;

public abstract class PriceFactory {
    private static final Map<Integer, Price> priceCache = new HashMap<>();

    public static Price makePrice(int cents) {
        return priceCache.computeIfAbsent(cents, Price::new);
    }

    public static Price makePrice(String stringValueIn) throws InvalidPriceException {
        if (stringValueIn == null || stringValueIn.isEmpty()) {
            throw new InvalidPriceException("Invalid price string: cannot be null or empty");
        }
        try {
            String sanitized = stringValueIn.replace("$", "").replace(",", "");
            int cents;
            if (sanitized.contains(".")) {
                String[] parts = sanitized.split("\\.", -1);
                if (parts.length != 2) {
                    throw new InvalidPriceException("Invalid price format: too many decimal places");
                }
                String centPartStr = parts[1].isEmpty() ? "00" : parts[1];
                if (centPartStr.length() == 1) {
                    centPartStr += "0";
                } else if (centPartStr.length() > 2) {
                    throw new InvalidPriceException("Invalid price format: too many decimal places");
                }
                int dollars = parts[0].isEmpty() || parts[0].equals("-") ? 0 : Integer.parseInt(parts[0]);
                int centPart = Integer.parseInt(centPartStr);
                cents = dollars * 100 + (dollars < 0 || parts[0].equals("-") ? -centPart : centPart);
            } else {
                cents = Integer.parseInt(sanitized) * 100;
            }
            return makePrice(cents);
        } catch (NumberFormatException e) {
            throw new InvalidPriceException("Invalid price string");
        }
    }
}

