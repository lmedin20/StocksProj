package interfaces;

import prices.GlobalConstants;
import exceptions.InvalidArgumentException;
import prices.Price;
import dto.TradableDTO;

public interface Tradable {
    String getId();
    int getRemainingVolume();
    void setCancelledVolume(int newVol) throws InvalidArgumentException;
    int getCancelledVolume();
    void setRemainingVolume(int newVol) throws InvalidArgumentException;
    TradableDTO makeTradableDTO();
    Price getPrice();
    void setFilledVolume(int newVol) throws InvalidArgumentException;
    int getFilledVolume();
    GlobalConstants.BookSide getSide();
    String getUser();
    String getProduct();
    int getOriginalVolume();
}