package com.edumanage.notificationservice.event;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class FeePaidEvent {
    private UUID paymentId;
    private UUID invoiceId;
    private UUID studentId;
    private BigDecimal paidAmount;
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime paidAt;
}
