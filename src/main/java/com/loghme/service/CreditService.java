package com.loghme.service;

import com.loghme.domain.utils.Loghme;
import com.loghme.domain.utils.exceptions.BadRequestException;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class CreditService {
    private Loghme loghme = Loghme.getInstance();
    @RequestMapping(value = "/credit", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public int addCredit(@RequestAttribute("claims") Claims claims, HttpEntity<String> httpEntity) throws SQLException {
        try {
            loghme.addCredit(claims.getId(), httpEntity.getBody());
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
        return loghme.getUserCredit(claims.getId());
    }
}
