package com.loghme.domain.schedulers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loghme.domain.utils.Food;
import com.loghme.domain.utils.Loghme;
import com.loghme.domain.utils.Restaurant;
import com.loghme.repository.LoghmeRepository;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class RestaurantsScheduler implements ServletContextListener {
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.schedule(new Callable() {
            public Object call() throws Exception {
                String loghmeBody = "";
                ArrayList<Restaurant> restaurants = null;
                Loghme loghme = Loghme.getInstance();
                LoghmeRepository loghmeRepository = LoghmeRepository.getInstance();
                ArrayList<Restaurant> convertedRestaurants;
                ObjectMapper nameMapper = new ObjectMapper();
                try {
                    loghmeBody = HTTPHandler.getUrlBody("http://138.197.181.131:8080/restaurants");
                    restaurants = nameMapper.readValue(loghmeBody, ArrayList.class);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                convertedRestaurants = nameMapper.convertValue(restaurants, new TypeReference<ArrayList<Restaurant>>() { });
                for (Restaurant restaurant: convertedRestaurants) {
                    LoghmeRepository loghmeRepo = LoghmeRepository.getInstance();
                    loghmeRepo.addRestaurant(restaurant.getId(), restaurant.getName(), restaurant.getLogo(), restaurant.getLocation().getX(), restaurant.getLocation().getY());
                    for (Food food: restaurant.getMenu()) {
                        loghmeRepo.addFood(restaurant.getId(), food.getName(), food.getDescription(), food.getPopularity(), food.getImage(), food.getPrice(), food.getCount());
                    }
//                    break;
                }

                loghmeRepository.addUser("1234", "Houman Chamani", "09300323231", "hoomch@gmail.com", 100000, "pass");
                return 0;
            }
        }, 0, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        scheduler.shutdownNow();
    }
}
