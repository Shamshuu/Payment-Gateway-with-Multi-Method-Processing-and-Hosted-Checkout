package com.gateway.controllers;

import com.gateway.dto.CreateOrderRequest;
import com.gateway.models.Merchant;
import com.gateway.models.Order;
import com.gateway.services.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(HttpServletRequest request, @RequestBody CreateOrderRequest body) {
        Merchant merchant = (Merchant) request.getAttribute("merchant");
        
        try {
            Order order = orderService.createOrder(merchant, body);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", Map.of(
                    "code", "BAD_REQUEST_ERROR",
                    "description", e.getMessage()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(HttpServletRequest request, @PathVariable String id) {
        Merchant merchant = (Merchant) request.getAttribute("merchant");
        Optional<Order> orderOpt = orderService.getOrder(id);

        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            // Ensure the order belongs to this merchant!
            if (!order.getMerchantId().equals(merchant.getId())) {
                return ResponseEntity.status(404).body(Map.of("error", Map.of("code", "NOT_FOUND_ERROR", "description", "Order not found")));
            }
            return ResponseEntity.ok(order);
        }

        return ResponseEntity.status(404).body(Map.of(
            "error", Map.of(
                "code", "NOT_FOUND_ERROR", 
                "description", "Order not found"
            )
        ));
    }
}