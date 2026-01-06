package com.gateway.config;

import com.gateway.models.Merchant;
import com.gateway.repositories.MerchantRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Optional;

@Component
public class AuthenticationFilter implements Filter {

    private final MerchantRepository merchantRepository;

    public AuthenticationFilter(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();

        // 1. Allow Public Endpoints (Health, CORS preflight, and future public checkout APIs)
        if (path.startsWith("/health") || 
            path.contains("/public/") || 
            httpRequest.getMethod().equals("OPTIONS")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Check Headers
        String apiKey = httpRequest.getHeader("X-Api-Key");
        String apiSecret = httpRequest.getHeader("X-Api-Secret");

        if (apiKey == null || apiSecret == null) {
            sendError(httpResponse, "AUTHENTICATION_ERROR", "Missing API credentials");
            return;
        }

        // 3. Validate against Database
        Optional<Merchant> merchantOpt = merchantRepository.findByApiKey(apiKey);
        
        if (merchantOpt.isPresent() && merchantOpt.get().getApiSecret().equals(apiSecret)) {
            // Authentication Success: Store merchant in request for the Controller to use
            request.setAttribute("merchant", merchantOpt.get());
            chain.doFilter(request, response);
        } else {
            sendError(httpResponse, "AUTHENTICATION_ERROR", "Invalid API credentials");
        }
    }

    private void sendError(HttpServletResponse response, String code, String desc) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": {\"code\": \"%s\", \"description\": \"%s\"}}", code, desc));
    }
}