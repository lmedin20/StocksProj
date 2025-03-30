package user;

import dto.TradableDTO;
import interfaces.CurrentMarketObserver;
import market.CurrentMarketSide;
import exceptions.DataValidationException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class User implements CurrentMarketObserver {
    private final String userId;
    private final Map<String, TradableDTO> tradables;
    private final HashMap<String, CurrentMarketSide[]> currentMarkets;

    public User(String userId) throws DataValidationException {
        validateUserId(userId);
        this.userId = userId;
        this.tradables = new HashMap<>();
        this.currentMarkets = new HashMap<>();
    }

    private void validateUserId(String userId) throws DataValidationException {
        if (userId == null || !Pattern.matches("^[A-Z]{3}$", userId)) {
            throw new DataValidationException("Invalid User ID: Must be exactly 3 uppercase letters (A-Z).");
        }
    }

    public void updateTradable(TradableDTO tradable) {
        if (tradable != null) {
            tradables.put(tradable.tradableId(), tradable);
        }
    }
    @Override
    public void updateCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide) {
        currentMarkets.put(symbol, new CurrentMarketSide[]{buySide, sellSide});
    }

    public String getCurrentMarkets() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, CurrentMarketSide[]> entry : currentMarkets.entrySet()) {
            sb.append(entry.getKey()).append(" ")
                    .append(entry.getValue()[0]).append(" - ")
                    .append(entry.getValue()[1]).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("User Id: ").append(userId).append("\n");

        for (TradableDTO tradable : tradables.values()) {
            sb.append(" Product: ").append(tradable.product())
                    .append(", Price: ").append(tradable.price().toString())
                    .append(", OriginalVolume: ").append(tradable.originalVolume())
                    .append(", RemainingVolume: ").append(tradable.remainingVolume())
                    .append(", CancelledVolume: ").append(tradable.cancelledVolume())
                    .append(", FilledVolume: ").append(tradable.filledVolume())
                    .append(", User: ").append(tradable.user())
                    .append(", Side: ").append(tradable.side())
                    .append(", Id: ").append(tradable.tradableId())
                    .append("\n");
        }

        return sb.toString();
    }

    public String getUserId() {
        return userId;
    }


    public Map<String, TradableDTO> getTradables() {
        return new HashMap<>(tradables);
    }
}

