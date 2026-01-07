package com.gateway.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Value("${gateway.test.merchant.key}")
    private String validKey;

    @Value("${gateway.test.merchant.secret}")
    private String validSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();

        // --- FIX 1: Allow OPTIONS requests (CORS Preflight) ---
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        // --- FIX 2: Existing Whitelist for Auth & Public ---
        if (path.startsWith("/api/v1/auth/") || path.contains("/public")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check for API Keys on protected endpoints
        if (path.startsWith("/api/v1/")) {
            String apiKey = request.getHeader("X-Api-Key");
            String apiSecret = request.getHeader("X-Api-Secret");
            
            if (apiKey == null || apiSecret == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing API Credentials");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}