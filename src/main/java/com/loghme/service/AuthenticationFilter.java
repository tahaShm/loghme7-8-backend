package com.loghme.service;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        chain.doFilter(request, response);
        System.out.println("filter called!");
        //Jwt checking must be implemented here...
    }
}
