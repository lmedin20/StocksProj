package prices;

import exceptions.InvalidArgumentException;
import exceptions.InvalidPriceException;
import dto.TradableDTO;
import interfaces.Tradable;

import java.util.Objects;

public class Order implements Tradable {
    private final String user;
    private final String product;
    private final Price price;
    private final GlobalConstants.BookSide side;
    private final int originalVolume;
    private int remainingVolume;
    private int cancelledVolume;
    private int filledVolume;
    private final String id;

    public Order(String user, String product, Price price, int originalVolume, GlobalConstants.BookSide side) throws InvalidPriceException, InvalidArgumentException {
        this.user = validateUser(user);
        this.product = validateProduct(product);
        this.price = validatePrice(price);
        this.side = validateSide(side);
        this.originalVolume = validateVolume(originalVolume);
        this.remainingVolume = originalVolume;
        this.cancelledVolume = 0;
        this.filledVolume = 0;
        this.id = generateId();
    }

    private String generateId() {
        return user + product + price.toString() + System.nanoTime();
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

    private GlobalConstants.BookSide validateSide(GlobalConstants.BookSide side) throws InvalidArgumentException {
        if (side == null) {
            throw new InvalidArgumentException("Side cannot be null.");
        }
        return side;
    }

    private int validateVolume(int volume) throws InvalidArgumentException {
        if (volume <= 0 || volume >= 10000) {
            throw new InvalidArgumentException("Volume must be between 1 and 9999.");
        }
        return volume;
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
        return String.format("%s %s order: %s at %s, Orig Vol: %d, Rem Vol: %d, Fill Vol: %d, CXL Vol: %d, ID: %s",
                user, side, product, price, originalVolume, remainingVolume, filledVolume, cancelledVolume, id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id.equals(order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
