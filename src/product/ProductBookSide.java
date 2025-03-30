package product;

import exceptions.DataValidationException;
import exceptions.InvalidArgumentException;
import exceptions.InvalidPriceException;
import dto.TradableDTO;
import interfaces.Tradable;
import prices.*;
import quote.QuoteSide;
import user.UserManager;

import java.util.*;

public class ProductBookSide {
    private final GlobalConstants.BookSide side;
    private final TreeMap<Price, ArrayList<Tradable>> bookEntries;

    public ProductBookSide(GlobalConstants.BookSide side) throws InvalidArgumentException {
        if (side == null) throw new InvalidArgumentException("BookSide cannot be null.");
        this.side = side;
        this.bookEntries = new TreeMap<Price, ArrayList<Tradable>>(
                side == GlobalConstants.BookSide.BUY ? Comparator.<Price>reverseOrder() : Comparator.<Price>naturalOrder()
        );
    }

    public TradableDTO add(Tradable t) throws DataValidationException {
        bookEntries.computeIfAbsent(t.getPrice(), k -> new ArrayList<>()).add(t);
        TradableDTO dto = new TradableDTO(t);

        UserManager.getInstance().updateTradable(t.getUser(), dto);
        return dto;
    }

    public TradableDTO cancel(String tradableId) throws InvalidArgumentException, DataValidationException {
        for (Price price : bookEntries.keySet()) {
            Iterator<Tradable> iterator = bookEntries.get(price).iterator();
            while (iterator.hasNext()) {
                Tradable t = iterator.next();
                if (t.getId().equals(tradableId)) {
                    //System.out.println("**CANCEL: " + t);
                    t.setCancelledVolume(t.getCancelledVolume() + t.getRemainingVolume());
                    t.setRemainingVolume(0);
                    iterator.remove();

                    if (bookEntries.get(price).isEmpty()) {
                        bookEntries.remove(price);
                    }

                    TradableDTO dto = new TradableDTO(t);
                    UserManager.getInstance().updateTradable(t.getUser(), dto);
                    return dto;
                }
            }
        }
        return null;
    }

    public TradableDTO removeQuotesForUser(String userName) throws InvalidArgumentException, DataValidationException {
        for (Price price : bookEntries.keySet()) {
            Iterator<Tradable> iterator = bookEntries.get(price).iterator();
            while (iterator.hasNext()) {
                Tradable t = iterator.next();
                if (t.getUser().equals(userName)) {
                    TradableDTO dto = cancel(t.getId());
                    return dto;
                }
            }
        }
        return null;
    }

    public void tradeOut(Price price, int volToTrade) throws InvalidArgumentException, DataValidationException {
        try {
            if (bookEntries.isEmpty() || bookEntries.firstKey().greaterThan(price)) return;
        } catch (InvalidPriceException e) {
            System.err.println("Error in tradeOut: " + e.getMessage());
            return;
        }

        List<Tradable> tradablesAtPrice = bookEntries.get(bookEntries.firstKey());
        int totalVolAtPrice = tradablesAtPrice.stream().mapToInt(Tradable::getRemainingVolume).sum();

        if (volToTrade >= totalVolAtPrice) {
            for (Tradable t : tradablesAtPrice) {
                int filledVolume = t.getRemainingVolume();

                t.setFilledVolume(t.getFilledVolume() + t.getRemainingVolume());
                t.setRemainingVolume(0);

                String tradeType = (t instanceof QuoteSide) ? "quote" : "order";

                System.out.printf(
                        "\tFULL FILL: (%s %3d) %s  %s side %s for %s: Price: %s, Orig Vol: %3d, Rem Vol: %3d, Fill Vol: %3d, Cxl'd Vol: %3d, ID: %s\n",
                        t.getSide(), filledVolume, t.getUser(), t.getSide(), tradeType, t.getProduct(),
                        t.getPrice(), t.getOriginalVolume(), t.getRemainingVolume(),
                        t.getFilledVolume(), t.getCancelledVolume(), t.getId()
                );

                UserManager.getInstance().updateTradable(t.getUser(), new TradableDTO(t));
            }
            bookEntries.remove(price);
            return;
        }

        int remainder = volToTrade;
        Iterator<Tradable> iterator = tradablesAtPrice.iterator();
        while (iterator.hasNext() && remainder > 0) {
            Tradable t = iterator.next();
            double ratio = (double) t.getRemainingVolume() / totalVolAtPrice;
            int toTrade = (int) Math.ceil(ratio * volToTrade);
            toTrade = Math.min(toTrade, remainder);
            toTrade = Math.min(toTrade, t.getRemainingVolume());

            int originalRemaining = t.getRemainingVolume();
            int filledVolume = toTrade;

            t.setFilledVolume(t.getFilledVolume() + toTrade);
            t.setRemainingVolume(t.getRemainingVolume() - toTrade);
            remainder -= toTrade;

            String tradeType = (t instanceof QuoteSide) ? "quote" : "order";

            if (t.getRemainingVolume() == 0) {
                System.out.printf(
                        "\tFULL FILL: (%s %3d) %s  %s side %s for %s: Price: %s, Orig Vol: %3d, Rem Vol: %3d, Fill Vol: %3d, Cxl'd Vol: %3d, ID: %s\n",
                        t.getSide(), filledVolume, t.getUser(), t.getSide(), tradeType, t.getProduct(),
                        t.getPrice(), t.getOriginalVolume(), t.getRemainingVolume(),
                        t.getFilledVolume(), t.getCancelledVolume(), t.getId()
                );
                iterator.remove();
            } else {
                System.out.printf(
                        "\tPARTIAL FILL: (%s %3d) %s  %s side %s for %s: Price: %s, Orig Vol: %3d, Rem Vol: %3d, Fill Vol: %3d, Cxl'd Vol: %3d, ID: %s\n",
                        t.getSide(), filledVolume, t.getUser(), t.getSide(), tradeType, t.getProduct(),
                        t.getPrice(), t.getOriginalVolume(), t.getRemainingVolume(),
                        t.getFilledVolume(), t.getCancelledVolume(), t.getId()
                );
            }

            UserManager.getInstance().updateTradable(t.getUser(), new TradableDTO(t));
        }

        if (tradablesAtPrice.isEmpty()) {
            bookEntries.remove(price);
        }
    }


    public int topOfBookVolume() {
        if (bookEntries.isEmpty()) return 0;
        return bookEntries.get(bookEntries.firstKey()).stream().mapToInt(Tradable::getRemainingVolume).sum();
    }

    public Price topOfBookPrice() {
        if (bookEntries.isEmpty()) return null;
        return bookEntries.firstKey();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Price, ArrayList<Tradable>> entry : bookEntries.entrySet()) {
            Price price = entry.getKey();
            ArrayList<Tradable> tradables = entry.getValue();

            sb.append("\t").append(price).append(":\n");

            for (Tradable t : tradables) {
                sb.append("\t\t").append(t.getUser()).append(" ")
                        .append(t.getSide()).append(" ")
                        .append((t instanceof Order) ? "side order" : "side quote")
                        .append(" for ").append(t.getProduct())
                        .append(": Price: ").append(t.getPrice())
                        .append(", Orig Vol: ").append(String.format("%4d", t.getOriginalVolume()))
                        .append(", Rem Vol: ").append(String.format("%4d", t.getRemainingVolume()))
                        .append(", Fill Vol: ").append(String.format("%4d", t.getFilledVolume()))
                        .append(", Cxl'd Vol: ").append(String.format("%4d", t.getCancelledVolume()))
                        .append(", ID: ").append(t.getId()).append("\n");
            }
        }

        return sb.toString();
    }

}



