package com.edumanage.feeservice.service;

import com.edumanage.feeservice.dto.InvoiceResponse;
import com.edumanage.feeservice.dto.PaymentRequest;
import com.edumanage.feeservice.dto.PaymentResponse;
import com.edumanage.feeservice.event.FeePaidEvent;
import com.edumanage.feeservice.event.StudentEnrolledEvent;
import com.edumanage.feeservice.exception.ResourceNotFoundException;
import com.edumanage.feeservice.mapper.FeeMapper;
import com.edumanage.feeservice.model.*;
import com.edumanage.feeservice.outbox.OutboxEvent;
import com.edumanage.feeservice.outbox.OutboxEventRepository;
import com.edumanage.feeservice.repository.FeeStructureRepository;
import com.edumanage.feeservice.repository.InvoiceRepository;
import com.edumanage.feeservice.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeeService {

    private final InvoiceRepository invoiceRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final PaymentRepository paymentRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final FeeMapper feeMapper;
    private final ObjectMapper objectMapper;

    private static final String FEE_PAID_TOPIC = "fee.paid";

    // Saga consumer: auto-generate TUITION invoice on enrollment
    @Transactional
    public void handleStudentEnrolled(StudentEnrolledEvent event) {
        log.info("Auto-generating invoice for studentId={}", event.getStudentId());

        // Attempt to find TUITION fee structure; skip gracefully if not configured
        feeStructureRepository.findAll().stream()
                .filter(fs -> fs.getFeeType() == FeeType.TUITION && fs.isActive())
                .findFirst()
                .ifPresent(feeStructure -> {
                    Invoice invoice = Invoice.builder()
                            .studentId(event.getStudentId())
                            .feeStructure(feeStructure)
                            .amount(feeStructure.getAmount())
                            .dueDate(LocalDate.now().plusDays(30))
                            .status(InvoiceStatus.PENDING)
                            .build();
                    invoiceRepository.save(invoice);
                    log.info("Invoice created for studentId={}", event.getStudentId());
                });
    }

    @Transactional
    public PaymentResponse pay(PaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + request.getInvoiceId()));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Invoice " + request.getInvoiceId() + " is already paid");
        }
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Invoice " + request.getInvoiceId() + " is cancelled");
        }

        Payment payment = Payment.builder()
                .invoice(invoice)
                .paidAmount(request.getPaidAmount())
                .paymentMethod(request.getPaymentMethod())
                .transactionId(request.getTransactionId())
                .remarks(request.getRemarks())
                .build();

        Payment saved;
        try {
            saved = paymentRepository.save(payment);
        } catch (DataIntegrityViolationException e) {
            // UNIQUE constraint on transaction_id — idempotent: duplicate submission
            throw new IllegalStateException(
                    "Payment already processed for transactionId: " + request.getTransactionId(), e);
        }
        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);

        // Outbox: persist event atomically with payment in the same transaction
        persistOutboxEvent(saved, invoice);

        log.info("Payment processed: invoiceId={}, amount={}", invoice.getId(), request.getPaidAmount());
        return feeMapper.toPaymentResponse(saved);
    }

    public List<InvoiceResponse> getInvoicesByStudent(UUID studentId) {
        return invoiceRepository.findByStudentId(studentId).stream()
                .map(feeMapper::toInvoiceResponse).toList();
    }

    public List<InvoiceResponse> getOutstanding() {
        return invoiceRepository.findByStatus(InvoiceStatus.PENDING).stream()
                .map(feeMapper::toInvoiceResponse).toList();
    }

    // Run every 6 hours — distributes load, avoids single midnight heap spike
    @Scheduled(cron = "0 0 0/6 * * *")
    @Transactional
    public void markOverdueInvoices() {
        // Single bulk UPDATE — no rows loaded into JVM heap
        int updated = invoiceRepository.bulkMarkOverdue(LocalDate.now());
        log.info("Marked {} invoices as OVERDUE", updated);
    }

    private void persistOutboxEvent(Payment payment, Invoice invoice) {
        FeePaidEvent event = FeePaidEvent.builder()
                .paymentId(payment.getId())
                .invoiceId(invoice.getId())
                .studentId(invoice.getStudentId())
                .paidAmount(payment.getPaidAmount())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .paidAt(LocalDateTime.now())
                .build();
        try {
            outboxEventRepository.save(OutboxEvent.builder()
                    .topic(FEE_PAID_TOPIC)
                    .aggregateId(invoice.getStudentId().toString())
                    .payload(objectMapper.writeValueAsString(event))
                    .build());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Outbox serialization failed", e);
        }
    }
}
