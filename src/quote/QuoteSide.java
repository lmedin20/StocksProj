package quote;

import dto.TradableDTO;
import interfaces.Tradable;
import prices.GlobalConstants;
import exceptions.InvalidArgumentException;
import exceptions.InvalidPriceException;
import prices.Price;

import java.util.Objects;

public class QuoteSide implements Tradable {
    private final String user;
    private final String product;
    private final Price price;
    private final GlobalConstants.BookSide side;
    private final int originalVolume;
    private int remainingVolume;
    private int cancelledVolume;
    private int filledVolume;
    private final String id;

    public QuoteSide(String user, String product, Price price, int originalVolume, GlobalConstants.BookSide side) throws InvalidPriceException, InvalidArgumentException {
        if (user == null || !user.matches("[A-Z]{3}")) {
            throw new InvalidArgumentException("User must be a 3-letter uppercase code.");
        }
        if (product == null || !product.matches("[A-Z0-9.]{1,5}")) {
            throw new InvalidArgumentException("Product symbol must be 1-5 alphanumeric characters.");
        }
        if (price == null) {
            throw new InvalidPriceException("Price cannot be null.");
        }
        if (side == null) {
            throw new InvalidArgumentException("Side cannot be null.");
        }
        if (originalVolume <= 0 || originalVolume >= 10000) {
            throw new InvalidArgumentException("Volume must be between 1 and 9999.");
        }

        this.user = user;
        this.product = product;
        this.price = price;
        this.side = side;
        this.originalVolume = originalVolume;
        this.remainingVolume = originalVolume;
        this.cancelledVolume = 0;
        this.filledVolume = 0;
        this.id = generateId();
    }

    private String generateId() {
        return user + product + price.toString() + System.nanoTime();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getRemainingVolume() {
        return remainingVolume;
    }

    @Override
    public void setCancelledVolume(int newVol) throws InvalidArgumentException {
        if (newVol < 0) {
            throw new InvalidArgumentException("Cancelled volume cannot be negative.");
        }
        this.cancelledVolume = newVol;
    }

    @Override
    public int getCancelledVolume() {
        return cancelledVolume;
    }

    @Override
    public void setRemainingVolume(int newVol) throws InvalidArgumentException {
        if (newVol < 0) {
            throw new InvalidArgumentException("Remaining volume cannot be negative.");
        }
        this.remainingVolume = newVol;
    }

    @Override
    public TradableDTO makeTradableDTO() {
        return new TradableDTO(this);
    }

    @Override
    public Price getPrice() {
        return price;
    }

    @Override
    public void setFilledVolume(int newVol) throws InvalidArgumentException {
        if (newVol < 0 || newVol > originalVolume) {
            throw new InvalidArgumentException("Filled volume must be between 0 and original volume.");
        }
        this.filledVolume = newVol;
    }

    @Override
    public int getFilledVolume() {
        return filledVolume;
    }

    @Override
    public GlobalConstants.BookSide getSide() {
        return side;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getProduct() {
        return product;
    }

    @Override
    public int getOriginalVolume() {
        return originalVolume;
    }

    @Override
    public String toString() {
        return String.format("%s %s side quote for %s: %s, Orig Vol: %d, Rem Vol: %d, Fill Vol: %d, CXL Vol: %d, ID: %s",
                user, side, product, price, originalVolume, remainingVolume, filledVolume, cancelledVolume, id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuoteSide quoteSide = (QuoteSide) o;
        return id.equals(quoteSide.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
