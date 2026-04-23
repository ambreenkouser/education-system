package com.edumanage.feeservice.controller;

import com.edumanage.feeservice.dto.InvoiceResponse;
import com.edumanage.feeservice.dto.PaymentRequest;
import com.edumanage.feeservice.dto.PaymentResponse;
import com.edumanage.feeservice.service.FeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fees")
@RequiredArgsConstructor
@Tag(name = "Fees", description = "Invoice and payment management")
public class FeeController {

    private final FeeService feeService;

    @PostMapping("/pay")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Process a payment (ACID + Outbox → fee.paid event)")
    public PaymentResponse pay(@Valid @RequestBody PaymentRequest request) {
        return feeService.pay(request);
    }

    @GetMapping("/invoices/student/{studentId}")
    @Operation(summary = "Get all invoices for a student")
    public List<InvoiceResponse> getInvoicesByStudent(@PathVariable UUID studentId) {
        return feeService.getInvoicesByStudent(studentId);
    }

    @GetMapping("/invoices/outstanding")
    @Operation(summary = "Get all outstanding (PENDING) invoices")
    public List<InvoiceResponse> getOutstanding() {
        return feeService.getOutstanding();
    }
}
