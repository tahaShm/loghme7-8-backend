package com.loghme.service;

import com.loghme.service.DTO.DTOHandler;
import com.loghme.service.DTO.FoodDTO;
import com.loghme.domain.utils.Loghme;
import com.loghme.domain.utils.exceptions.BadRequestException;
import io.jsonwebtoken.Claims;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
public class FoodService {
    private Loghme loghme = Loghme.getInstance();

    @RequestMapping(value = "/food/{id}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<FoodDTO> addFood(
            @RequestAttribute("claims") Claims claims,
            @PathVariable(value = "id") String id,
            @RequestParam(value = "foodName") String foodName,
            @RequestParam(value = "count") int count) throws SQLException {
        try {
            loghme.changeCart(claims.getId(), id, foodName, count, false);
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
        return loghme.getCurrentOrderFoods(claims.getId());
    }

    @RequestMapping(value = "/food/{id}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<FoodDTO> deleteFood(
            @RequestAttribute("claims") Claims claims,
            @PathVariable(value = "id") String id,
            @RequestParam(value = "foodName") String foodName,
            @RequestParam(value = "count") int count) throws SQLException {
        try {
            loghme.changeCart(claims.getId(), id, foodName, -count, false);
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
        return loghme.getCurrentOrderFoods(claims.getId());
    }

    @RequestMapping(value = "/food", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<FoodDTO> getFoods(
            @RequestParam(value = "id") String id) {
        ArrayList<FoodDTO> foods = null;
        try {
            foods = DTOHandler.getRestaurantFoods(id);
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
        return foods;
    }
}