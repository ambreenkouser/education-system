package com.edumanage.reportservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report_fee_summary")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeeSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID studentId;
    private UUID invoiceId;
    private BigDecimal paidAmount;
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime paidAt;
}
