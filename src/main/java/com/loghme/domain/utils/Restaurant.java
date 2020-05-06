package com.loghme.domain.utils;

import java.util.ArrayList;

public class Restaurant {
    private String id;
    private String name;
    private Location location;
    private String logo;
    private ArrayList<Food> menu = new ArrayList<>();
    private ArrayList<PartyFood> partyFoods = new ArrayList<>();

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

    public ArrayList<Food> getMenu() {
        return menu;
    }

    public void setMenu (ArrayList<Food> menu) {
        this.menu = menu;
    }

    public ArrayList<PartyFood> getPartyFoods() { return partyFoods; }

    public void setPartyFoods(ArrayList<PartyFood> partyFoods) { this.partyFoods = partyFoods; }
}
