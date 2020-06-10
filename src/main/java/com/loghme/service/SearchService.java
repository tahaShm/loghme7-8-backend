package com.loghme.service;

import com.loghme.service.DTO.DTOHandler;
import com.loghme.service.DTO.RestaurantDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
public class SearchService {
    @RequestMapping(value = "/search", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<RestaurantDTO> getRestaurantById(
            @RequestParam(value = "restaurantName") String restaurantName,
            @RequestParam(value = "foodName") String foodName) throws SQLException {
        return DTOHandler.getSearchedRestaurants(restaurantName, foodName);
    }
}
