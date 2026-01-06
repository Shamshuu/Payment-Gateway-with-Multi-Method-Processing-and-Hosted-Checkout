package com.gateway.services;

import com.gateway.dto.CreatePaymentRequest;
import com.gateway.models.Merchant;
import com.gateway.models.Order;
import com.gateway.models.Payment;
import com.gateway.repositories.OrderRepository;
import com.gateway.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ValidationService validationService;
    private final IdGenerator idGenerator;
    private final Random random = new Random();

    // Configuration Injection
    @Value("${gateway.test.mode}")
    private boolean testMode;

    @Value("${gateway.test.payment.success}")
    private boolean testPaymentSuccess;

    @Value("${gateway.test.processing.delay}")
    private long testProcessingDelay;

    @Value("${gateway.payment.upi.success-rate}")
    private double upiSuccessRate;

    @Value("${gateway.payment.card.success-rate}")
    private double cardSuccessRate;

    @Value("${gateway.simulation.delay.min}")
    private long delayMin;

    @Value("${gateway.simulation.delay.max}")
    private long delayMax;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository,
                          ValidationService validationService, IdGenerator idGenerator) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.validationService = validationService;
        this.idGenerator = idGenerator;
    }

    public Payment processPayment(Merchant merchant, CreatePaymentRequest request) throws Exception {
        // 1. Validate Order
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // If merchant is provided (Authenticated API call), validate ownership
        if (merchant != null && !order.getMerchantId().equals(merchant.getId())) {
            throw new IllegalArgumentException("Order does not belong to this merchant");
        }

        // 2. Validate Payment Method
        Payment payment = new Payment();
        payment.setId(idGenerator.generate("pay_"));
        payment.setOrderId(order.getId());
        payment.setMerchantId(order.getMerchantId());
        payment.setAmount(order.getAmount());
        payment.setCurrency(order.getCurrency());
        payment.setMethod(request.getMethod());
        payment.setStatus("processing"); // Mandatory initial status

        if ("upi".equalsIgnoreCase(request.getMethod())) {
            if (!validationService.isValidVpa(request.getVpa())) {
                throw new IllegalArgumentException("INVALID_VPA");
            }
            payment.setVpa(request.getVpa());
        } else if ("card".equalsIgnoreCase(request.getMethod())) {
            if (request.getCard() == null || 
                !validationService.isValidLuhn(request.getCard().getNumber()) ||
                !validationService.isValidExpiry(request.getCard().getExpiryMonth(), request.getCard().getExpiryYear())) {
                throw new IllegalArgumentException("INVALID_CARD");
            }
            payment.setCardNetwork(validationService.getCardNetwork(request.getCard().getNumber()));
            String num = request.getCard().getNumber().replaceAll("[\\s-]", "");
            payment.setCardLast4(num.substring(num.length() - 4));
        } else {
            throw new IllegalArgumentException("Invalid payment method");
        }

        // 3. Persist Initial State
        payment = paymentRepository.save(payment);

        // 4. Simulate Processing (Delay)
        simulateDelay();

        // 5. Determine Outcome
        boolean isSuccess = determineOutcome(request.getMethod());

        // 6. Update Final Status
        if (isSuccess) {
            payment.setStatus("success");
            // Also update order status
            order.setStatus("paid");
            orderRepository.save(order);
        } else {
            payment.setStatus("failed");
            payment.setErrorCode("PAYMENT_FAILED");
            payment.setErrorDescription("Transaction declined by bank");
        }
        
        // Update timestamp manually since we are modifying same object
        payment.setUpdatedAt(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }
    
    public Optional<Payment> getPayment(String id) {
        return paymentRepository.findById(id);
    }

    private void simulateDelay() {
        try {
            long delay = testMode ? testProcessingDelay : (delayMin + random.nextLong(delayMax - delayMin));
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean determineOutcome(String method) {
        if (testMode) {
            return testPaymentSuccess;
        }
        double threshold = "upi".equalsIgnoreCase(method) ? upiSuccessRate : cardSuccessRate;
        return random.nextDouble() < threshold; // e.g., 0.85 < 0.90 (Success)
    }
}