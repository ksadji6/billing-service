package com.esmt.billing.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreditRequest {
    private Long userId;
    private BigDecimal amount;
}