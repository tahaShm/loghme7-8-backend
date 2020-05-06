package com.loghme.domain.schedulers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loghme.domain.utils.Loghme;
import com.loghme.domain.utils.Restaurant;

import java.sql.SQLException;
import java.util.ArrayList;

public class PartySchedulerRunnable implements Runnable {
    @Override
    public void run() {
        ObjectMapper nameMapper = new ObjectMapper();
        ArrayList<Restaurant> tempRestaurants = null;
        try {
            String body = HTTPHandler.getUrlBody("http://138.197.181.131:8080/foodparty");
            body = body.replaceAll("menu", "partyFoods");
            body = body.replaceAll("price", "newPrice");
            body = body.replaceAll("oldPrice", "price");
            tempRestaurants = nameMapper.readValue(body, ArrayList.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<Restaurant> partyRestaurants = nameMapper.convertValue(tempRestaurants, new TypeReference<ArrayList<Restaurant>>() { });
        try {
            Loghme.getInstance().addPartyRestaurants(partyRestaurants);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Loghme.getInstance().setPartyStartTime();
    }
}
