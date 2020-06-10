package com.loghme.service;

import com.loghme.domain.utils.exceptions.BadRequestException;
import com.loghme.service.DTO.DTOHandler;
import com.loghme.service.DTO.RestaurantDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
public class RestaurantService {
    @RequestMapping(value = "/restaurant", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<RestaurantDTO> getRestaurants(@RequestParam(value = "showLevel") int showLevel) throws SQLException {
        System.out.println("restaurants here");
        return DTOHandler.getRestaurantsOnLevel(showLevel);
    }

    @RequestMapping(value = "/restaurant/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RestaurantDTO getRestaurantById(
            @PathVariable(value = "id") String id) {
        try {
            return DTOHandler.getRestaurantById(id);
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
    }
}
