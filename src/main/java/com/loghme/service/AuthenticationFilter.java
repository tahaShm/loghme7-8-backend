package com.loghme.service;

import com.loghme.domain.utils.Loghme;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        "/credit"
})
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String authTokenHeader = req.getHeader("Authorization");
        String[] parts = authTokenHeader.split(" ");
        String token = parts[1];

        try {
            Loghme.getInstance().decodeJWT(token);
            chain.doFilter(request, response);
            System.out.println("token is valid!");
        }
        catch (Exception e) {
            System.out.println("token is invalid!");
        }
    }
}
