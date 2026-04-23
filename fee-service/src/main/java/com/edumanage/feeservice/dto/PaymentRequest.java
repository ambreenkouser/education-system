package com.edumanage.feeservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentRequest {
    @NotNull private UUID invoiceId;
    @NotNull @DecimalMin("0.01") private BigDecimal paidAmount;
    @NotBlank private String paymentMethod;
    private String transactionId;
    private String remarks;
}
