package com.loghme.service;

import com.loghme.domain.utils.Loghme;
import com.loghme.service.DTO.OrderDTO;
import com.loghme.domain.utils.exceptions.BadRequestException;
import io.jsonwebtoken.Claims;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
public class OrderService {
    private Loghme loghme = Loghme.getInstance();

    @RequestMapping(value = "/order", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<OrderDTO> getOrders(@RequestAttribute("claims") Claims claims) throws SQLException {
        return loghme.getUserOrders(claims.getId());
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void finalizeOrder(@RequestAttribute("claims") Claims claims) {
        try {
            loghme.finalizeOrder(claims.getId());
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
    }
}
