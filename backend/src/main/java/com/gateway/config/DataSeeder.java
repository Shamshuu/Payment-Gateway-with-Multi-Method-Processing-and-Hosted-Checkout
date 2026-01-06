package com.gateway.config;

import com.gateway.models.Merchant;
import com.gateway.repositories.MerchantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class DataSeeder implements CommandLineRunner {

    private final MerchantRepository merchantRepository;

    @Value("${gateway.test.merchant.email}")
    private String testEmail;

    @Value("${gateway.test.merchant.key}")
    private String testApiKey;

    @Value("${gateway.test.merchant.secret}")
    private String testApiSecret;

    public DataSeeder(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (merchantRepository.findByEmail(testEmail).isEmpty()) {
            Merchant testMerchant = new Merchant();
            // Hardcoded UUID as per requirements
            testMerchant.setId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")); 
            testMerchant.setName("Test Merchant");
            testMerchant.setEmail(testEmail);
            testMerchant.setApiKey(testApiKey);
            testMerchant.setApiSecret(testApiSecret);
            testMerchant.setActive(true);
            
            merchantRepository.save(testMerchant);
            System.out.println("✅ Test Merchant seeded successfully.");
        } else {
            System.out.println("ℹ️ Test Merchant already exists.");
        }
    }
}