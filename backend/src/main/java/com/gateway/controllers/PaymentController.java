package com.gateway.controllers;

import com.gateway.dto.PaymentRequest;
import com.gateway.models.Merchant;
import com.gateway.models.Payment;
import com.gateway.repositories.MerchantRepository;
import com.gateway.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final MerchantRepository merchantRepository; // Added Dependency

    // Updated Constructor to include MerchantRepository
    public PaymentController(PaymentService paymentService, MerchantRepository merchantRepository) {
        this.paymentService = paymentService;
        this.merchantRepository = merchantRepository;
    }

    @PostMapping("/public")
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest request) {
        Payment payment = paymentService.processPayment(request);
        return ResponseEntity.ok(Map.of(
            "id", payment.getId(),
            "status", payment.getStatus(),
            "error_description", "failed".equals(payment.getStatus()) ? "Payment declined by bank" : ""
        ));
    }

    @GetMapping("/{id}/public")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String id) {
        Payment payment = paymentService.getPayment(id);
        return ResponseEntity.ok(Map.of(
            "id", payment.getId(),
            "status", payment.getStatus(),
            "error_description", "failed".equals(payment.getStatus()) ? "Payment declined by bank" : ""
        ));
    }

    // --- NEW ENDPOINT: LIST TRANSACTIONS ---
    @GetMapping
    public ResponseEntity<?> getAllPayments(@RequestHeader("X-Api-Key") String apiKey) {
        // 1. Find the merchant who owns this API Key
        // (Using stream filter since we haven't added findByApiKey to repo yet)
        Merchant merchant = merchantRepository.findAll().stream()
                .filter(m -> m.getApiKey().equals(apiKey))
                .findFirst()
                .orElse(null);

        if (merchant == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid API Key"));
        }

        // 2. Fetch payments for this specific merchant
        List<Payment> payments = paymentService.getPaymentsForMerchant(merchant.getId());
        
        return ResponseEntity.ok(payments);
    }
}