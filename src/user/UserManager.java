package user;

import dto.TradableDTO;
import exceptions.DataValidationException;

import java.util.Map;
import java.util.TreeMap;

public class UserManager {
    private static UserManager instance;
    private final Map<String, User> users;


    private UserManager() {
        users = new TreeMap<>();
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }


    public void init(String[] usersIn) throws DataValidationException {
        if (usersIn == null) {
            throw new DataValidationException("User list cannot be null.");
        }
        for (String userId : usersIn) {
            try {
                users.put(userId, new User(userId));
            } catch (DataValidationException e) {
                System.out.println("Skipping invalid user ID: " + userId);
            }
        }
    }


    public void updateTradable(String userId, TradableDTO tradable) throws DataValidationException {
        if (userId == null) {
            throw new DataValidationException("User ID cannot be null.");
        }
        if (tradable == null) {
            throw new DataValidationException("TradableDTO cannot be null.");
        }
        User user = users.get(userId);
        if (user == null) {
            throw new DataValidationException("User does not exist: " + userId);
        }
        user.updateTradable(tradable);
    }

    public User getUser(String userId) throws DataValidationException {
        User user = users.get(userId);
        if (user == null) {
            throw new DataValidationException("User does not exist: " + userId);
        }
        return user;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (User user : users.values()) {
            sb.append(user.toString()).append("\n");
        }
        return sb.toString();
    }
}

