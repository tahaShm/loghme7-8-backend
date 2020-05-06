package com.loghme.service.DTO;

import java.util.ArrayList;

public class OrderDTO {
    private String status;
    private String restaurantName;
    private ArrayList<FoodDTO> foods;

    public OrderDTO() {}

    public OrderDTO(String status, String restaurantName, ArrayList<FoodDTO> foods) {
        this.status = status;
        this.restaurantName = restaurantName;
        this.foods = foods;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getRestaurantName() { return restaurantName; }

    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public ArrayList<FoodDTO> getFoods() { return foods; }

    public void setFoods(ArrayList<FoodDTO> foods) { this.foods = foods; }
}
