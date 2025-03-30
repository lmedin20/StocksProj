package product;

import exceptions.DataValidationException;
import exceptions.InvalidArgumentException;
import exceptions.InvalidPriceException;
import quote.Quote;
import dto.TradableDTO;
import interfaces.Tradable;
import prices.*;
import user.UserManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProductManager {
    private static ProductManager instance;
    private final Map<String, ProductBook> productBooks;
    private final Random random;

    private ProductManager() {
        productBooks = new HashMap<>();
        random = new Random();
    }

    public static synchronized ProductManager getInstance() {
        if (instance == null) {
            instance = new ProductManager();
        }
        return instance;
    }

    public void addProduct(String symbol) throws DataValidationException, InvalidArgumentException {
        if (symbol == null || !symbol.matches("^[A-Z0-9]{1,5}(\\.[A-Z0-9]{1,5})?$")) {
            throw new DataValidationException("Invalid product symbol: " + symbol);
        }
        if (!productBooks.containsKey(symbol)) {
            productBooks.put(symbol, new ProductBook(symbol));
        }
    }

    public ProductBook getProductBook(String symbol) throws DataValidationException {
        ProductBook book = productBooks.get(symbol);
        if (book == null) {
            throw new DataValidationException("Product not found: " + symbol);
        }
        return book;
    }

    public String getRandomProduct() throws DataValidationException {
        if (productBooks.isEmpty()) {
            throw new DataValidationException("No products available.");
        }
        Object[] keys = productBooks.keySet().toArray();
        return (String) keys[random.nextInt(keys.length)];
    }

    public TradableDTO addTradable(Tradable tradable) throws DataValidationException, InvalidArgumentException, InvalidPriceException {
        if (tradable == null) {
            throw new DataValidationException("Tradable cannot be null.");
        }
        ProductBook book = getProductBook(tradable.getProduct());
        TradableDTO dto = book.add(tradable);
        UserManager.getInstance().updateTradable(tradable.getUser(), dto);
        return dto;
    }

    public TradableDTO[] addQuote(Quote quote) throws DataValidationException, InvalidArgumentException, InvalidPriceException {
        if (quote == null) {
            throw new DataValidationException("Quote cannot be null.");
        }
        ProductBook book = getProductBook(quote.getSymbol());
        book.removeQuotesForUser(quote.getUser());
        return new TradableDTO[]{
                addTradable(quote.getQuoteSide(GlobalConstants.BookSide.BUY)),
                addTradable(quote.getQuoteSide(GlobalConstants.BookSide.SELL))
        };
    }

    public TradableDTO cancel(TradableDTO tradableDTO) throws DataValidationException, InvalidArgumentException, InvalidPriceException {
        if (tradableDTO == null) {
            throw new DataValidationException("TradableDTO cannot be null.");
        }
        return getProductBook(tradableDTO.product()).cancel(tradableDTO.side(), tradableDTO.tradableId());
    }

    public TradableDTO[] cancelQuote(String symbol, String user) throws DataValidationException, InvalidArgumentException, InvalidPriceException {
        if (symbol == null || user == null) {
            throw new DataValidationException("Symbol and User cannot be null.");
        }
        return getProductBook(symbol).removeQuotesForUser(user);
    }

    @Override
    public String toString() {
        return String.join("\n", productBooks.values().stream().map(ProductBook::toString).toList());
    }
}

