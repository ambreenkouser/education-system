package com.edumanage.feeservice.repository;

import com.edumanage.feeservice.model.Invoice;
import com.edumanage.feeservice.model.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    List<Invoice> findByStudentId(UUID studentId);
    List<Invoice> findByStudentIdAndStatus(UUID studentId, InvoiceStatus status);
    List<Invoice> findByStatus(InvoiceStatus status);

    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :today AND i.status = 'PENDING'")
    List<Invoice> findOverdue(LocalDate today);
}
