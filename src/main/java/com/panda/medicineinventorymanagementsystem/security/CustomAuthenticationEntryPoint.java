package com.panda.medicineinventorymanagementsystem.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        if (authException instanceof UsernameNotFoundException) {
            response.getWriter().write("{\"error\": \"User not found.\"}");
        } else if (authException instanceof BadCredentialsException) {
            response.getWriter().write("{\"error\": \"Incorrect password provided.\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"You need to log in to access this resource.\"}");
        }
        response.getWriter().flush();
    }
}


