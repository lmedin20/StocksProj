package market;

import prices.Price;

public class CurrentMarketSide {
    private final Price price;
    private final int volume;

    public CurrentMarketSide(Price price, int volume) {
        this.price = price;
        this.volume = volume;
    }

    @Override
    public String toString() {
        String priceStr = (price != null) ? price.toString() : "$0.00";
        return priceStr + "x" + volume;
    }
}

