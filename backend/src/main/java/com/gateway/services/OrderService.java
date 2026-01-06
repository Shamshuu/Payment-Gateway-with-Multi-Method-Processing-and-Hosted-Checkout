package com.gateway.services;

import com.gateway.dto.CreateOrderRequest;
import com.gateway.models.Merchant;
import com.gateway.models.Order;
import com.gateway.repositories.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final IdGenerator idGenerator;
    private final ObjectMapper objectMapper = new ObjectMapper(); // For converting notes to JSON string

    public OrderService(OrderRepository orderRepository, IdGenerator idGenerator) {
        this.orderRepository = orderRepository;
        this.idGenerator = idGenerator;
    }

    public Order createOrder(Merchant merchant, CreateOrderRequest request) throws Exception {
        if (request.getAmount() == null || request.getAmount() < 100) {
            throw new IllegalArgumentException("amount must be at least 100");
        }

        Order order = new Order();
        order.setId(idGenerator.generate("order_"));
        order.setMerchantId(merchant.getId());
        order.setAmount(request.getAmount());
        order.setCurrency(request.getCurrency() != null ? request.getCurrency() : "INR");
        order.setReceipt(request.getReceipt());
        order.setStatus("created");
        
        if (request.getNotes() != null) {
            order.setNotes(objectMapper.writeValueAsString(request.getNotes()));
        }

        return orderRepository.save(order);
    }

    public Optional<Order> getOrder(String orderId) {
        return orderRepository.findById(orderId);
    }
}