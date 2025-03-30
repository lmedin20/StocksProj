package product;

import exceptions.DataValidationException;
import exceptions.InvalidArgumentException;
import exceptions.InvalidPriceException;
import market.CurrentMarketTracker;
import quote.Quote;
import dto.TradableDTO;
import interfaces.Tradable;
import prices.*;

public class ProductBook {
    private final String product;
    private final ProductBookSide buySide;
    private final ProductBookSide sellSide;

    public ProductBook(String product) throws InvalidArgumentException {
        if (product == null || !product.matches("[A-Z0-9.]{1,5}")) {
            throw new InvalidArgumentException("Invalid product symbol (1-5 alphanumeric characters).");
        }
        this.product = product;
        this.buySide = new ProductBookSide(GlobalConstants.BookSide.BUY);
        this.sellSide = new ProductBookSide(GlobalConstants.BookSide.SELL);
    }

    private void updateMarket() throws InvalidPriceException {
        Price buyPrice = (buySide.topOfBookPrice() != null) ? buySide.topOfBookPrice() : PriceFactory.makePrice(0);
        int buyVolume = buySide.topOfBookVolume();
        Price sellPrice = (sellSide.topOfBookPrice() != null) ? sellSide.topOfBookPrice() : PriceFactory.makePrice(0);
        int sellVolume = sellSide.topOfBookVolume();

        CurrentMarketTracker.getInstance().updateMarket(product, buyPrice, buyVolume, sellPrice, sellVolume);
    }

    public TradableDTO add(Tradable t) throws InvalidArgumentException, DataValidationException, InvalidPriceException {
        if (t == null) throw new InvalidArgumentException("Tradable cannot be null.");
        TradableDTO dto = (t.getSide() == GlobalConstants.BookSide.BUY) ? buySide.add(t) : sellSide.add(t);
        tryTrade();
        updateMarket();
        return new TradableDTO(t);
    }


    public TradableDTO[] add(Quote qte) throws InvalidArgumentException, DataValidationException, InvalidPriceException {
        if (qte == null) throw new InvalidArgumentException("Quote cannot be null.");
        removeQuotesForUser(qte.getUser());
        TradableDTO buyDTO = buySide.add(qte.getQuoteSide(GlobalConstants.BookSide.BUY));
        TradableDTO sellDTO = sellSide.add(qte.getQuoteSide(GlobalConstants.BookSide.SELL));
        tryTrade();
        updateMarket();
        return new TradableDTO[]{buyDTO, sellDTO};
    }

    public TradableDTO cancel(GlobalConstants.BookSide side, String orderId) throws InvalidArgumentException, DataValidationException, InvalidPriceException {
        TradableDTO cancelledOrder = (side == GlobalConstants.BookSide.BUY) ? buySide.cancel(orderId) : sellSide.cancel(orderId);
        if (cancelledOrder == null) {
            System.out.println("Failed to cancel " + side + " order\n");
        }
        updateMarket();
        return cancelledOrder;
    }

    public TradableDTO[] removeQuotesForUser(String userName) throws InvalidArgumentException, DataValidationException, InvalidPriceException {
        if (userName == null) {
            System.out.println("Failed to cancel null quote\n");
            return new TradableDTO[]{null, null};
        }
        TradableDTO buyDTO = buySide.removeQuotesForUser(userName);
        TradableDTO sellDTO = sellSide.removeQuotesForUser(userName);
        updateMarket();
        return new TradableDTO[]{buyDTO, sellDTO};
    }

    public void tryTrade() throws InvalidArgumentException, DataValidationException, InvalidPriceException {
        while (true) {
            Price topBuyPrice = buySide.topOfBookPrice();
            Price topSellPrice = sellSide.topOfBookPrice();
            try {
                if (topBuyPrice == null || topSellPrice == null || topSellPrice.greaterThan(topBuyPrice)) {
                    return;
                }
            } catch (InvalidPriceException e) {
                System.err.println("Error in tryTrade: " + e.getMessage());
                return;
            }
            int totalToTrade = Math.min(buySide.topOfBookVolume(), sellSide.topOfBookVolume());
            buySide.tradeOut(topBuyPrice, totalToTrade);
            sellSide.tradeOut(topBuyPrice, totalToTrade);
        }
    }

    public String getTopOfBookString(GlobalConstants.BookSide side) {
        Price price = (side == GlobalConstants.BookSide.BUY) ? buySide.topOfBookPrice() : sellSide.topOfBookPrice();
        int volume = (side == GlobalConstants.BookSide.BUY) ? buySide.topOfBookVolume() : sellSide.topOfBookVolume();
        return (price != null) ? String.format("Top of %s book: %s x %d", side, price, volume) : String.format("Top of %s book:  $0.00 x 0", side);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------\n");
        sb.append("Product Book: ").append(product).append("\n");
        sb.append("Side: BUY\n");
        sb.append(buySide.toString().isEmpty() ? "\t<Empty>\n" : buySide.toString());
        sb.append("Side: SELL\n");
        sb.append(sellSide.toString().isEmpty() ? "\t<Empty>\n" : sellSide.toString());
        sb.append("--------------------------------------------\n");
        return sb.toString();
    }
}

