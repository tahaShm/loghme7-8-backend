package com.loghme.service;

import com.loghme.service.DTO.FoodDTO;
import com.loghme.domain.utils.Loghme;
import io.jsonwebtoken.Claims;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
public class CurrentOrderService {
    private Loghme loghme = Loghme.getInstance();
    @RequestMapping(value = "/currentOrder", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<FoodDTO> getCurrentOrder(@RequestAttribute("claims") Claims claims) throws SQLException {
        return loghme.getCurrentOrderFoods(claims.getId());
    }
}