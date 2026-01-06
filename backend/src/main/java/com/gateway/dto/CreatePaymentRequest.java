package com.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreatePaymentRequest {
    @JsonProperty("order_id")
    private String orderId;

    private String method; // "upi" or "card"
    
    private String vpa;

    private CardDetails card;

    @Data
    public static class CardDetails {
        private String number;
        
        @JsonProperty("expiry_month")
        private String expiryMonth;
        
        @JsonProperty("expiry_year")
        private String expiryYear;
        
        private String cvv;
        
        @JsonProperty("holder_name")
        private String holderName;
    }
}