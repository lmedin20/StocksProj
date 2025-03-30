package market;

import exceptions.InvalidPriceException;
import prices.Price;

public class CurrentMarketTracker {
    private static final CurrentMarketTracker instance = new CurrentMarketTracker();

    private CurrentMarketTracker() {}

    public static CurrentMarketTracker getInstance() {
        return instance;
    }

    public void updateMarket(String symbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) throws InvalidPriceException {
        double marketWidth = 0.00;

        if (buyPrice != null && sellPrice != null && buyPrice.toDouble() > 0 && sellPrice.toDouble() > 0) {
            marketWidth = sellPrice.subtract(buyPrice).toDouble();
        }

        CurrentMarketSide buySide = new CurrentMarketSide(buyPrice, buyVolume);
        CurrentMarketSide sellSide = new CurrentMarketSide(sellPrice, sellVolume);

        System.out.println("*********** Current Market ***********");
        System.out.printf("* %s   %s - %s [$%.2f]%n", symbol, buySide, sellSide, marketWidth);
        System.out.println("**************************************");

        CurrentMarketPublisher.getInstance().acceptCurrentMarket(symbol, buySide, sellSide);
    }

}
