package com.gateway.controllers;

import com.gateway.repositories.MerchantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    private final MerchantRepository merchantRepository;
    
    @Value("${gateway.test.merchant.email}")
    private String testEmail;

    public TestController(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @GetMapping("/merchant")
    public ResponseEntity<?> getTestMerchant() {
        return merchantRepository.findByEmail(testEmail)
                .map(m -> ResponseEntity.ok(Map.of(
                        "id", m.getId(),
                        "email", m.getEmail(),
                        "api_key", m.getApiKey(),
                        "seeded", true
                )))
                .orElse(ResponseEntity.notFound().build());
    }
}