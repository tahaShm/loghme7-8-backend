package com.loghme.service;

import com.loghme.service.DTO.DTOHandler;
import com.loghme.service.DTO.FoodDTO;
import com.loghme.domain.utils.Loghme;
import com.loghme.service.DTO.PartyFoodDTO;
import com.loghme.domain.utils.exceptions.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class PartyFoodService {
    private Loghme loghme = Loghme.getInstance();

    @RequestMapping(value = "/partyFood/{id}", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<FoodDTO> addFood(
            @PathVariable(value = "id") String id,
            @RequestParam(value = "foodName") String foodName,
            @RequestParam(value = "count") int count) {
        try {
            loghme.changeCart("1234", id, foodName, count, true);
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
        return loghme.getCurrentOrderFoods("1234");
    }

    @RequestMapping(value = "/partyFood/{id}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<FoodDTO> deleteFood(
            @PathVariable(value = "id") String id,
            @RequestParam(value = "foodName") String foodName,
            @RequestParam(value = "count") int count) {
        try {
            loghme.changeCart("1234", id, foodName, -count, true);
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
        return loghme.getCurrentOrderFoods("1234");
    }

    @RequestMapping(value = "/partyFood", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<PartyFoodDTO> getFoods() {
        try {
            return DTOHandler.getPartyFoods();
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
    }

    @RequestMapping(value = "/partyFood/time", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public int getPartyRemainingTime() {
        return (600 - (int) ((System.currentTimeMillis() - loghme.getPartyStartTime()) / 1000));
    }
}
