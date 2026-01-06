package com.gateway.controllers;

import com.gateway.dto.CreatePaymentRequest;
import com.gateway.models.Merchant;
import com.gateway.models.Payment;
import com.gateway.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments")
    public ResponseEntity<?> createPayment(HttpServletRequest request, @RequestBody CreatePaymentRequest body) {
        Merchant merchant = (Merchant) request.getAttribute("merchant");
        try {
            // Note: If calling from public checkout, merchant might be null, handled in Service
            Payment payment = paymentService.processPayment(merchant, body);
            return ResponseEntity.status(HttpStatus.CREATED).body(payment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", Map.of("code", "BAD_REQUEST_ERROR", "description", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/payments/{id}")
    public ResponseEntity<?> getPayment(@PathVariable String id) {
        Optional<Payment> payment = paymentService.getPayment(id);
        if (payment.isPresent()) {
            return ResponseEntity.ok(payment.get());
        }
        return ResponseEntity.status(404).body(Map.of("error", Map.of("code", "NOT_FOUND_ERROR", "description", "Payment not found")));
    }
    
    // PUBLIC ENDPOINTS FOR CHECKOUT PAGE
    
    @PostMapping("/payments/public")
    public ResponseEntity<?> createPublicPayment(@RequestBody CreatePaymentRequest body) {
        try {
            // Pass null for merchant to indicate public context
            Payment payment = paymentService.processPayment(null, body);
            return ResponseEntity.status(HttpStatus.CREATED).body(payment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", Map.of("code", "BAD_REQUEST_ERROR", "description", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/payments/{id}/public")
    public ResponseEntity<?> getPublicPaymentStatus(@PathVariable String id) {
        Optional<Payment> payment = paymentService.getPayment(id);
        if (payment.isPresent()) {
            // Return minimal info for security
            return ResponseEntity.ok(Map.of(
                "id", payment.get().getId(),
                "status", payment.get().getStatus(),
                "error_description", payment.get().getErrorDescription() != null ? payment.get().getErrorDescription() : ""
            ));
        }
        return ResponseEntity.status(404).body(Map.of("error", "Payment not found"));
    }

    @GetMapping("/payments")
    public ResponseEntity<?> getAllPayments(HttpServletRequest request) {
        Merchant merchant = (Merchant) request.getAttribute("merchant");
        return ResponseEntity.ok(paymentService.getPaymentsForMerchant(merchant));
    }
}