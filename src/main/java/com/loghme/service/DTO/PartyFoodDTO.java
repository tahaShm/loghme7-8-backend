package com.loghme.service.DTO;

import com.loghme.domain.utils.PartyFood;
import com.loghme.repository.DAO.PartyFoodDAO;

public class PartyFoodDTO {
    private PartyFoodDAO food;
    private String restaurantName;
    private String restaurantId;

    public PartyFoodDTO(PartyFoodDAO food, String restaurantName, String restaurantId) {
        this.food = food;
        this.restaurantName = restaurantName;
        this.restaurantId = restaurantId;
    }

    public PartyFoodDAO getFood() { return food; }

    public void setFood(PartyFoodDAO food) { this.food = food; }

    public String getRestaurantName() { return restaurantName; }

    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getRestaurantId() { return restaurantId; }

    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
}
