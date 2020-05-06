package com.loghme.domain.utils;

import com.loghme.domain.schedulers.CouriersScheduler;
import com.loghme.domain.utils.exceptions.*;
import com.loghme.repository.LoghmeRepository;
import com.loghme.service.DTO.FoodDTO;
import com.loghme.service.DTO.OrderDTO;
import com.loghme.service.DTO.UserDTO;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.*;

public class Loghme
{
    private static Loghme singleApp = null;
    private LoghmeRepository loghmeRepository = LoghmeRepository.getInstance();
    private long partyStartTime;

    public static Loghme getInstance() {
        if (singleApp == null)
            singleApp = new Loghme();

        return singleApp;
    }

    public void setPartyStartTime() {
        partyStartTime = System.currentTimeMillis();
    }

    public long getPartyStartTime() { return partyStartTime; }

    public void changeCart(String username, String restaurantId, String foodName, int count, boolean isPartyFood) throws FoodFromOtherRestaurantInCartExp, ExtraFoodPartyExp, NotEnoughFoodToDelete {
        String currentOrderRestaurantId = loghmeRepository.getCurrentOrderRestaurantId(username);
        if (currentOrderRestaurantId == null || currentOrderRestaurantId.equals(restaurantId)) {
            if (isPartyFood)
                loghmeRepository.changeCurrentOrder(username, foodName, restaurantId, count, "party");
            else
                loghmeRepository.changeCurrentOrder(username, foodName, restaurantId, count, "normal");
        }
        else
            throw new FoodFromOtherRestaurantInCartExp();
    }

    public void finalizeOrder(String username) throws NotEnoughCreditExp, RestaurantNotFoundExp {
        int orderId = loghmeRepository.finalizeOrder(username);
        Location location = loghmeRepository.getOrderRestaurantLocation(orderId);
        Timer timer = new Timer();
        TimerTask task = new CouriersScheduler(location, orderId);
        timer.schedule(task, 0, 3000);
    }

    public void addCredit(String username, String json) throws JSONException, NotEnoughCreditExp {
        JSONObject obj = new JSONObject(json);
        loghmeRepository.changeCredit(username, obj.getInt("credit"));
    }

    public int getUserCredit(String username) {
        return loghmeRepository.getCredit(username);
    }

    public UserDTO getUserDTO(String username) {
        return loghmeRepository.getUserDTO(username);
    }

    public ArrayList<OrderDTO> getUserOrders(String username) {
        return loghmeRepository.getOrders(username);
    }

    public ArrayList<FoodDTO> getCurrentOrderFoods(String username) {
        return loghmeRepository.getCurrentOrderFoods(username);
    }

    public void addPartyRestaurants(ArrayList<Restaurant> partyRestaurants) throws SQLException {
        loghmeRepository.invalidPrevPartyFoods();
        for (Restaurant restaurant: partyRestaurants) {
            loghmeRepository.addRestaurant(restaurant.getId(), restaurant.getName(), restaurant.getLogo(), restaurant.getLocation().getX(), restaurant.getLocation().getY());
            for (PartyFood partyFood: restaurant.getPartyFoods()) {
                int foodId = loghmeRepository.addFood(restaurant.getId(), partyFood.getName(), partyFood.getDescription(), partyFood.getPopularity(), partyFood.getImage(), partyFood.getPrice(), partyFood.getCount());
                loghmeRepository.addPartyFood(restaurant.getId(), foodId, partyFood.getNewPrice(), partyFood.getCount());

            }
        }
    }
}