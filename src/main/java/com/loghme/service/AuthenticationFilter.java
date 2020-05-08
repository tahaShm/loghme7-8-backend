package com.loghme.service;

import com.loghme.domain.utils.Loghme;
import io.jsonwebtoken.Claims;

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
        "/credit",
        "/profile"
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
            System.out.println(authTokenHeader);
            if (authTokenHeader.length() > 7) { //Bearer
                String[] parts = authTokenHeader.split(" ");
                String token = parts[1];
                Claims claims = Loghme.getInstance().decodeJWT(token);
                req.setAttribute("claims", claims);
                chain.doFilter(request, response);
            }
            else {
                //null token -> 401
                System.out.println("empty token");
            }
        }
        catch (Exception e) {
            //invalid token -> 403
            System.out.println("token is invalid!");
        }
    }
}
