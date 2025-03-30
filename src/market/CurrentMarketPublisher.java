package market;
import interfaces.CurrentMarketObserver;
import market.CurrentMarketSide;

import java.util.*;
import java.util.ArrayList;

public class CurrentMarketPublisher {
    private static final CurrentMarketPublisher instance = new CurrentMarketPublisher();
    private final Map<String, List<CurrentMarketObserver>> filters = new HashMap<>();

    private CurrentMarketPublisher() {}

    public static CurrentMarketPublisher getInstance() {
        return instance;
    }

    public void subscribeCurrentMarket(String symbol, CurrentMarketObserver observer) {
        filters.computeIfAbsent(symbol, k -> new ArrayList<>()).add(observer);
    }

    public void unSubscribeCurrentMarket(String symbol, CurrentMarketObserver observer) {
        List<CurrentMarketObserver> observers = filters.get(symbol);
        if (observers != null) {
            observers.remove(observer);
            if (observers.isEmpty()) {
                filters.remove(symbol);
            }
        }
    }

    public void acceptCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide) {
        List<CurrentMarketObserver> observers = filters.get(symbol);
        if (observers != null) {
            for (CurrentMarketObserver observer : observers) {
                observer.updateCurrentMarket(symbol, buySide, sellSide);
            }
        }
    }
}
