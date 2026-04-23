package com.edumanage.feeservice.repository;

import com.edumanage.feeservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByInvoiceId(UUID invoiceId);
}
