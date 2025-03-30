package quote;

import prices.GlobalConstants;
import exceptions.InvalidArgumentException;
import exceptions.InvalidPriceException;
import prices.Price;

public class Quote {
    private final String user;
    private final String product;
    private final QuoteSide buySide;
    private final QuoteSide sellSide;

    public Quote(String product, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume, String user) throws InvalidPriceException, InvalidArgumentException {
        this.user = validateUser(user);
        this.product = validateProduct(product);
        this.buySide = new QuoteSide(user, product, validatePrice(buyPrice), validateVolume(buyVolume), GlobalConstants.BookSide.BUY);
        this.sellSide = new QuoteSide(user, product, validatePrice(sellPrice), validateVolume(sellVolume), GlobalConstants.BookSide.SELL);
    }

    private String validateUser(String user) throws InvalidArgumentException {
        if (user == null || !user.matches("[A-Z]{3}")) {
            throw new InvalidArgumentException("User must be a 3-letter uppercase code.");
        }
        return user;
    }

    private String validateProduct(String product) throws InvalidArgumentException {
        if (product == null || !product.matches("[A-Z0-9.]{1,5}")) {
            throw new InvalidArgumentException("Product symbol must be 1-5 alphanumeric characters.");
        }
        return product;
    }

    private Price validatePrice(Price price) throws InvalidPriceException {
        if (price == null) {
            throw new InvalidPriceException("Price cannot be null.");
        }
        return price;
    }

    private int validateVolume(int volume) throws InvalidArgumentException {
        if (volume <= 0 || volume >= 10000) {
            throw new InvalidArgumentException("Volume must be between 1 and 9999.");
        }
        return volume;
    }

    public QuoteSide getQuoteSide(GlobalConstants.BookSide side) throws InvalidArgumentException {
        if (side == GlobalConstants.BookSide.BUY) {
            return buySide;
        } else if (side == GlobalConstants.BookSide.SELL) {
            return sellSide;
        } else {
            throw new InvalidArgumentException("Invalid BookSide");
        }
    }

    public String getSymbol() {
        return product;
    }

    public String getUser() {
        return user;
    }
}
