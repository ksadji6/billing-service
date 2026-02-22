package com.esmt.billing.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class DebitResponse {
    private String txnRef;
    private String status; // SUCCESS, FAILED
    private BigDecimal newBalance;
    private boolean fallback;
}