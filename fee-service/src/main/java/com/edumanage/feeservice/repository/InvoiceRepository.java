package com.edumanage.feeservice.repository;

import com.edumanage.feeservice.model.Invoice;
import com.edumanage.feeservice.model.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    List<Invoice> findByStudentId(UUID studentId);
    List<Invoice> findByStudentIdAndStatus(UUID studentId, InvoiceStatus status);
    List<Invoice> findByStatus(InvoiceStatus status);

    /** Single bulk UPDATE — replaces the JPA loop that loaded all rows into heap */
    @Modifying
    @Query("UPDATE Invoice i SET i.status = 'OVERDUE', i.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE i.status = 'PENDING' AND i.dueDate < :today")
    int bulkMarkOverdue(LocalDate today);
}
