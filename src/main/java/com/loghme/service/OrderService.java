package com.loghme.service;

import com.loghme.domain.utils.Loghme;
import com.loghme.service.DTO.OrderDTO;
import com.loghme.domain.utils.exceptions.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class OrderService {
    private Loghme loghme = Loghme.getInstance();

    @RequestMapping(value = "/order", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<OrderDTO> getOrders() {
        return loghme.getUserOrders("1234");
    }

    @RequestMapping(value = "/order", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void finalizeOrder() {
        try {
            loghme.finalizeOrder("1234");
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
    }
}
