package com.edumanage.feeservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InvoiceResponse {
    private UUID id;
    private UUID studentId;
    private UUID feeStructureId;
    private String feeType;
    private BigDecimal amount;
    private LocalDate dueDate;
    private String status;
    private LocalDateTime createdAt;
}
