package com.loghme.service;

import com.loghme.domain.utils.Loghme;
import com.loghme.domain.utils.exceptions.BadRequestException;
import com.loghme.domain.utils.exceptions.DuplicateEmail;
import com.loghme.service.DTO.UserDTO;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
public class ProfileService {
    @RequestMapping(value = "/profile", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getUser(@RequestAttribute("claims") Claims claims) throws SQLException {
        return Loghme.getInstance().getUserDTO(claims.getId());
    }
}
