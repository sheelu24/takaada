package com.takaada.integration.service;

import com.takaada.integration.entities.*;
import com.takaada.integration.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

//Computes financial insights from data
@Service
public class InsightService {

    private final CustomerRepository customerRepo;
    private final InvoiceRepository invoiceRepo;
    private final PaymentRepository paymentRepo;

    public InsightService(CustomerRepository customerRepo, InvoiceRepository invoiceRepo, PaymentRepository paymentRepo) {
        this.customerRepo = customerRepo;
        this.invoiceRepo = invoiceRepo;
        this.paymentRepo = paymentRepo;
    }

     //Outstanding balance for a customer
    @Transactional(readOnly = true)
    public BigDecimal getOutstandingBalanceForCustomer(Long customerId) {
        Customer customer = customerRepo.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        List<Invoice> invoices = invoiceRepo.findByCustomerId(customerId);
        BigDecimal totalInvoiced = invoices.stream()
            .map(Invoice::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPaid = invoices.stream()
            .flatMap(inv -> paymentRepo.findByInvoiceId(inv.getId()).stream())
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalInvoiced.subtract(totalPaid);
    }

    //Invoices that are past due and not fully paid 
    @Transactional(readOnly = true)
    public List<OverdueInvoiceDto> getOverdueInvoices() {
        LocalDate today = LocalDate.now();
        List<Invoice> overdue = invoiceRepo.findOverdueInvoices(today);

        return overdue.stream()
            .map(inv -> {
                BigDecimal paid = paymentRepo.findByInvoiceId(inv.getId()).stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal balance = inv.getAmount().subtract(paid);
                long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(inv.getDueDate(), today);
                return new OverdueInvoiceDto(
                    inv.getId(),
                    inv.getCustomer().getId(),
                    inv.getExternalId(),
                    inv.getAmount(),
                    inv.getCurrency(),
                    inv.getDueDate(),
                    inv.getStatus(),
                    balance,
                    daysOverdue
                );
            })
            .collect(Collectors.toList());
    }

    public record OverdueInvoiceDto(
        Long id,
        Long customerId,
        String externalId,
        BigDecimal amount,
        String currency,
        LocalDate dueDate,
        String status,
        BigDecimal outstandingBalance,
        long daysOverdue
    ) {}
}
