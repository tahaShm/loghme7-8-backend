package com.loghme.service;

import com.loghme.domain.utils.Loghme;
import com.loghme.domain.utils.exceptions.BadRequestException;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreditService {
    private Loghme loghme = Loghme.getInstance();
    @RequestMapping(value = "/credit", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public int addCredit(HttpEntity<String> httpEntity) {
        try {
            loghme.addCredit("1234", httpEntity.getBody());
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
        return loghme.getUserCredit("1234");
    }
}
