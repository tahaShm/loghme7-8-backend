package com.loghme.service.DTO;

import com.loghme.domain.utils.Location;
import com.loghme.domain.utils.Restaurant;
import com.loghme.repository.DAO.FoodDAO;

import java.util.ArrayList;

public class RestaurantDTO {
    private String id;
    private String name;
    private Location location;
    private String logo;
    private ArrayList<FoodDAO> menu;

    public RestaurantDTO() {}

    public RestaurantDTO(String id, String name, float x, float y, String logo, ArrayList<FoodDAO> menu) {
        this.id = id;
        this.name = name;
        this.location = new Location();
        this.location.setX(x);
        this.location.setY(y);
        this.logo = logo;
        this.menu = menu;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ArrayList<FoodDAO> getMenu() {
        return menu;
    }

    public void setMenu(ArrayList<FoodDAO> menu) {
        this.menu = menu;
    }
}