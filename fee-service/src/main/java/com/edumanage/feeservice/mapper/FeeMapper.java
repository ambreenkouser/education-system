package com.edumanage.feeservice.mapper;

import com.edumanage.feeservice.dto.InvoiceResponse;
import com.edumanage.feeservice.dto.PaymentResponse;
import com.edumanage.feeservice.model.Invoice;
import com.edumanage.feeservice.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeeMapper {

    @Mapping(target = "feeStructureId", expression = "java(invoice.getFeeStructure().getId())")
    @Mapping(target = "feeType",        expression = "java(invoice.getFeeStructure().getFeeType().name())")
    @Mapping(target = "status",         expression = "java(invoice.getStatus().name())")
    InvoiceResponse toInvoiceResponse(Invoice invoice);

    @Mapping(target = "invoiceId",  expression = "java(payment.getInvoice().getId())")
    @Mapping(target = "studentId",  expression = "java(payment.getInvoice().getStudentId())")
    PaymentResponse toPaymentResponse(Payment payment);
}
