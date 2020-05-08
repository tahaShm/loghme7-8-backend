package com.loghme.service;

import com.loghme.domain.utils.Loghme;
import com.loghme.domain.utils.exceptions.BadRequestException;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationService {
    @RequestMapping(value = "/signup", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public String signUp(HttpEntity<String> httpEntity) {
        String token = null;
        try {
            token = Loghme.getInstance().addUser(httpEntity.getBody());
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
        return token;
    }
    @RequestMapping(value = "/login", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public String login(HttpEntity<String> httpEntity) {
        String token = null;
        try {
            token = Loghme.getInstance().loginUser(httpEntity.getBody());
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
        return token;
    }

    @RequestMapping(value = "/checkAuth", method = RequestMethod.GET)
    public void isLoggedIn() {}
}
