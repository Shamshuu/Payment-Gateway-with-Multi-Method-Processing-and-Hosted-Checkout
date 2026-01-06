package com.gateway.dto;

import lombok.Data;
import java.util.Map;

@Data
public class CreateOrderRequest {
    private Long amount; // in paise
    private String currency;
    private String receipt;
    private Map<String, Object> notes;
}