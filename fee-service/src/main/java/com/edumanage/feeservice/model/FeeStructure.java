package com.edumanage.feeservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "fee_structures")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String gradeLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeType feeType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    private String description;

    @Column(nullable = false)
    private boolean active = true;
}
