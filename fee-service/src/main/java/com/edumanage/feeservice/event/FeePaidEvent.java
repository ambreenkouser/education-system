package com.edumanage.feeservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FeePaidEvent {
    private UUID paymentId;
    private UUID invoiceId;
    private UUID studentId;
    private BigDecimal paidAmount;
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime paidAt;
}
