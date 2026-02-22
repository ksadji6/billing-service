package com.esmt.billing.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DebitRequest {
    private Long userId;
    private BigDecimal amount;
    private String tripRef; // Pour lier la transaction au trajet
    private boolean isFallback; // Transmis par le Trip Service si tarif de secours appliqué
}