package com.loghme.service;

import com.loghme.domain.utils.Loghme;
import com.loghme.repository.LoghmeRepository;
import com.loghme.service.DTO.UserDTO;
import io.jsonwebtoken.Claims;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

@WebFilter(urlPatterns = {"/search/",
        "/search",
        "/restaurant/",
        "/restaurant",
        "/restaurant/*",
        "/partyFood/",
        "/partyFood",
        "/partyFood/*",
        "/order/",
        "/order",
        "/food/",
        "/food",
        "/food/*",
        "/currentOrder/",
        "/currentOrder",
        "/credit/",
        "/credit",
        "/profile",
        "/checkAuth"
})
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        try {
            String authTokenHeader = req.getHeader("Authorization");
            if (authTokenHeader.length() > 7) { //Bearer
                String[] parts = authTokenHeader.split(" ");
                String token = parts[1];
//                token = Loghme.getInstance().createJWT("aaa", "bbb", 86400000);
                Claims claims = Loghme.getInstance().decodeJWT(token);
                String userEmail = claims.getId();
                LoghmeRepository loghmeRepo = LoghmeRepository.getInstance();
                UserDTO currentUser = loghmeRepo.getUserDTO(userEmail);
                if (claims.getExpiration().getTime() < System.currentTimeMillis()) {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // -> 401, expired
                }
                else if (currentUser  != null) {
//                    System.out.println(claims);
                    req.setAttribute("claims", claims);
                    chain.doFilter(request, response);
                }
                else {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN); // -> 403
                }
            }
            else {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // -> 401
            }
        }
        catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN); // -> 403
        }
    }
}
