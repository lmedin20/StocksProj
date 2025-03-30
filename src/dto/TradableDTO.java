package dto;

import interfaces.Tradable;
import prices.GlobalConstants;
import prices.Price;

public record TradableDTO(
        String user,
        String product,
        Price price,
        int originalVolume,
        int remainingVolume,
        int cancelledVolume,
        int filledVolume,
        GlobalConstants.BookSide side,
        String tradableId
) {
    public TradableDTO(Tradable tradable) {
        this(
                tradable.getUser(),
                tradable.getProduct(),
                tradable.getPrice(),
                tradable.getOriginalVolume(),
                tradable.getRemainingVolume(),
                tradable.getCancelledVolume(),
                tradable.getFilledVolume(),
                tradable.getSide(),
                tradable.getId()
        );
    }
}
