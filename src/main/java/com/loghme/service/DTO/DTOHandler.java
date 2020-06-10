package com.loghme.service.DTO;

import com.loghme.domain.utils.*;
import com.loghme.domain.utils.exceptions.RestaurantNotFoundExp;
import com.loghme.repository.DAO.FoodDAO;
import com.loghme.repository.DAO.PartyFoodDAO;
import com.loghme.repository.DAO.RestaurantDAO;
import com.loghme.repository.LoghmeRepository;

import java.sql.SQLException;
import java.util.ArrayList;

public class DTOHandler {

    public static ArrayList<PartyFoodDTO> getPartyFoods() throws SQLException {
        ArrayList<PartyFoodDTO> retPartyFoods = new ArrayList<>();
        LoghmeRepository loghmeRepo = LoghmeRepository.getInstance();
        ArrayList<PartyFoodDAO> partyFoods = loghmeRepo.getValidPartyFoods();
        for (PartyFoodDAO partyFood: partyFoods) {
            RestaurantDAO currentRestaurant = loghmeRepo.getRestaurantByPartyFoodId(partyFood.getId());
            retPartyFoods.add(new PartyFoodDTO(partyFood, currentRestaurant.getName(), currentRestaurant.getId()));
        }
        return retPartyFoods;
    }

    public static ArrayList<FoodDTO> getRestaurantFoods(String id) throws SQLException {
        ArrayList<FoodDTO> toReturn = new ArrayList<>();
        LoghmeRepository loghmeRepo = LoghmeRepository.getInstance();
        ArrayList<FoodDAO> foods = loghmeRepo.getRestaurantFoods(id);
        for (FoodDAO food: foods) {
            toReturn.add(new FoodDTO(food.getName(), food.getPrice(), food.getDescription(), food.getPopularity(), food.getImageUrl()));
        }
        return toReturn;
    }

    public static ArrayList<RestaurantDTO> getRestaurantsOnLevel(int showLevel) throws SQLException {
        ArrayList<RestaurantDTO> toReturn = new ArrayList<>();
        LoghmeRepository loghmeRepo = LoghmeRepository.getInstance();
        ArrayList<RestaurantDAO> restaurants = loghmeRepo.getRestaurantsOnLevel(showLevel * 12);
        for (RestaurantDAO restaurant: restaurants) {
            toReturn.add(new RestaurantDTO(restaurant.getId(), restaurant.getName(), restaurant.getX(), restaurant.getY(), restaurant.getLogoUrl(), loghmeRepo.getRestaurantFoods(restaurant.getId())));
        }
        return toReturn;
    }

    public static RestaurantDTO getRestaurantById(String id) throws RestaurantNotFoundExp, SQLException {
        LoghmeRepository loghmeRepo = LoghmeRepository.getInstance();
        RestaurantDAO restaurant = loghmeRepo.getRestaurantById(id);
        if (restaurant == null)
            throw new RestaurantNotFoundExp();
        return new RestaurantDTO(restaurant.getId(), restaurant.getName(), restaurant.getX(), restaurant.getY(), restaurant.getLogoUrl(), loghmeRepo.getRestaurantFoods(restaurant.getId()));
    }

    public static ArrayList<RestaurantDTO> getSearchedRestaurants(String restaurantName, String foodName) throws SQLException {
        ArrayList<RestaurantDTO> toReturn = new ArrayList<>();
        LoghmeRepository loghmeRepo = LoghmeRepository.getInstance();
        ArrayList<RestaurantDAO> restaurants = loghmeRepo.getSearchedRestaurants(restaurantName, foodName);
        for (RestaurantDAO restaurant: restaurants) {
            toReturn.add(new RestaurantDTO(restaurant.getId(), restaurant.getName(), restaurant.getX(), restaurant.getY(), restaurant.getLogoUrl(), loghmeRepo.getRestaurantFoods(restaurant.getId())));
        }
        return toReturn;
    }
}
