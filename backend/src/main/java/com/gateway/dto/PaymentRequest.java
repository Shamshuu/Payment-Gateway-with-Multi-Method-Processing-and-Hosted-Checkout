package com.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentRequest {

    @JsonProperty("order_id")
    private String orderId;

    private String method; // "upi" or "card"
    
    private String vpa; // For UPI
    private String email; 

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getVpa() { return vpa; }
    public void setVpa(String vpa) { this.vpa = vpa; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}