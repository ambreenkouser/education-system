package com.edumanage.feeservice;

import com.edumanage.feeservice.dto.PaymentRequest;
import com.edumanage.feeservice.model.*;
import com.edumanage.feeservice.outbox.OutboxEventRepository;
import com.edumanage.feeservice.repository.FeeStructureRepository;
import com.edumanage.feeservice.repository.InvoiceRepository;
import com.edumanage.feeservice.repository.PaymentRepository;
import com.edumanage.feeservice.service.FeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeeServiceTest {

    @Mock InvoiceRepository invoiceRepository;
    @Mock PaymentRepository paymentRepository;
    @Mock FeeStructureRepository feeStructureRepository;
    @Mock OutboxEventRepository outboxEventRepository;
    @Mock ObjectMapper objectMapper;
    @InjectMocks FeeService feeService;

    @Test
    void pay_whenInvoiceNotFound_throwsIllegalArgument() {
        UUID invoiceId = UUID.randomUUID();
        PaymentRequest req = new PaymentRequest();
        req.setInvoiceId(invoiceId);
        req.setPaidAmount(BigDecimal.valueOf(500));
        req.setPaymentMethod("CASH");
        req.setTransactionId("TXN-001");

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> feeService.pay(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invoice not found");
    }

    @Test
    void pay_whenInvoiceAlreadyPaid_throwsIllegalState() {
        UUID invoiceId = UUID.randomUUID();
        FeeStructure fs = FeeStructure.builder()
                .id(UUID.randomUUID()).gradeLevel("GRADE_10")
                .feeType(FeeType.TUITION).amount(BigDecimal.valueOf(5000)).active(true).build();
        Invoice invoice = Invoice.builder()
                .id(invoiceId).studentId(UUID.randomUUID())
                .feeStructure(fs).amount(BigDecimal.valueOf(5000))
                .dueDate(LocalDate.now().plusDays(30)).status(InvoiceStatus.PAID).build();

        PaymentRequest req = new PaymentRequest();
        req.setInvoiceId(invoiceId);
        req.setPaidAmount(BigDecimal.valueOf(5000));
        req.setPaymentMethod("CASH");
        req.setTransactionId("TXN-002");

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        assertThatThrownBy(() -> feeService.pay(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already paid");
    }

    @Test
    void pay_whenValid_savesPaymentAndMarksInvoicePaid() throws Exception {
        UUID invoiceId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        FeeStructure fs = FeeStructure.builder()
                .id(UUID.randomUUID()).gradeLevel("GRADE_10")
                .feeType(FeeType.TUITION).amount(BigDecimal.valueOf(5000)).active(true).build();
        Invoice invoice = Invoice.builder()
                .id(invoiceId).studentId(studentId)
                .feeStructure(fs).amount(BigDecimal.valueOf(5000))
                .dueDate(LocalDate.now().plusDays(30)).status(InvoiceStatus.PENDING).build();

        PaymentRequest req = new PaymentRequest();
        req.setInvoiceId(invoiceId);
        req.setPaidAmount(BigDecimal.valueOf(5000));
        req.setPaymentMethod("BANK_TRANSFER");
        req.setTransactionId("TXN-003");

        Payment savedPayment = Payment.builder()
                .id(UUID.randomUUID()).invoice(invoice)
                .paidAmount(req.getPaidAmount()).paymentMethod(req.getPaymentMethod())
                .transactionId(req.getTransactionId()).build();

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(paymentRepository.save(any())).thenReturn(savedPayment);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        feeService.pay(req);

        verify(paymentRepository).save(any(Payment.class));
        verify(invoiceRepository).save(argThat(inv -> inv.getStatus() == InvoiceStatus.PAID));
        verify(outboxEventRepository).save(any());
    }
}
