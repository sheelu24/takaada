package com.takaada.integration.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takaada.integration.entities.*;
import com.takaada.integration.integration.dto.*;
import com.takaada.integration.repository.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

//Adapter for the external accounting API
@Component
public class AccountingApiAdapter {

    private static final String SOURCE = "ACCOUNTING_API";

    private final RawIntegrationDataRepository rawRepo;
    private final CustomerRepository customerRepo;
    private final InvoiceRepository invoiceRepo;
    private final PaymentRepository paymentRepo;
    private final ObjectMapper objectMapper;

    public AccountingApiAdapter(RawIntegrationDataRepository rawRepo,
                                CustomerRepository customerRepo,
                                InvoiceRepository invoiceRepo,
                                PaymentRepository paymentRepo,
                                ObjectMapper objectMapper) {
        this.rawRepo = rawRepo;
        this.customerRepo = customerRepo;
        this.invoiceRepo = invoiceRepo;
        this.paymentRepo = paymentRepo;
        this.objectMapper = objectMapper;
    }

    // Fetches customers, invoices, and payments from the external API, stores raw payloads
    public void sync() {

    	//Get data from exposed API
        List<AccountingApiCustomerDto> customerDtos = fetchCustomers();

        List<AccountingApiInvoiceDto> invoiceDtos = fetchInvoices();

        List<AccountingApiPaymentDto> paymentDtos = fetchPayments();

        //Write raw rows for audit and re-processing
        for (AccountingApiCustomerDto dto : customerDtos) {
            saveRaw("CUSTOMER", dto.getId(), dto);
        }
        for (AccountingApiInvoiceDto dto : invoiceDtos) {
            saveRaw("INVOICE", dto.getId(), dto);
        }
        for (AccountingApiPaymentDto dto : paymentDtos) {
            saveRaw("PAYMENT", dto.getId(), dto);
        }

        //Map to common entities and save
        for (AccountingApiCustomerDto dto : customerDtos) {
            Customer c = customerRepo.findBySourceAndExternalId(SOURCE, dto.getId())
                .orElse(new Customer(SOURCE, dto.getId(), dto.getName()));
            c.setName(dto.getName());
            customerRepo.save(c);
        }

        for (AccountingApiInvoiceDto dto : invoiceDtos) {
            Customer customer = customerRepo.findBySourceAndExternalId(SOURCE, dto.getCustomerId())
                .orElseThrow(() -> new IllegalStateException("Customer not found: " + dto.getCustomerId()));
            Invoice inv = invoiceRepo.findBySourceAndExternalId(SOURCE, dto.getId())
                .orElse(new Invoice(customer, SOURCE, dto.getId(), dto.getAmount(), dto.getCurrency(), dto.getDueDate(), dto.getStatus()));
            inv.setCustomer(customer);
            inv.setAmount(dto.getAmount());
            inv.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "USD");
            inv.setDueDate(dto.getDueDate());
            inv.setStatus(dto.getStatus() != null ? dto.getStatus() : "ISSUED");
            invoiceRepo.save(inv);
        }

        for (AccountingApiPaymentDto dto : paymentDtos) {
            Invoice invoice = invoiceRepo.findBySourceAndExternalId(SOURCE, dto.getInvoiceId())
                .orElseThrow(() -> new IllegalStateException("Invoice not found: " + dto.getInvoiceId()));
            Payment pay = paymentRepo.findBySourceAndExternalId(SOURCE, dto.getId())
                .orElse(new Payment(invoice, SOURCE, dto.getId(), dto.getAmount(), dto.getPaidAt()));
            pay.setInvoice(invoice);
            pay.setAmount(dto.getAmount());
            pay.setPaidAt(dto.getPaidAt());
            paymentRepo.save(pay);
        }
    }

    private void saveRaw(String entityType, String externalId, Object dto) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize raw payload", e);
        }
        rawRepo.save(new RawIntegrationData(SOURCE, entityType, externalId, payload));
    }

    // Dummy data
    private List<AccountingApiCustomerDto> fetchCustomers() {
        return List.of(
            new AccountingApiCustomerDto("ext-cust-1", "Acme Corp"),
            new AccountingApiCustomerDto("ext-cust-2", "Beta Inc")
        );
    }
    
    private List<AccountingApiInvoiceDto> fetchInvoices() {
        LocalDate pastDue = LocalDate.now().minusDays(10);
        LocalDate futureDue = LocalDate.now().plusDays(30);
        return List.of(
            new AccountingApiInvoiceDto("ext-inv-1", "ext-cust-1", new BigDecimal("500.00"), "USD", pastDue, "ISSUED"),
            new AccountingApiInvoiceDto("ext-inv-2", "ext-cust-1", new BigDecimal("200.00"), "USD", futureDue, "ISSUED"),
            new AccountingApiInvoiceDto("ext-inv-3", "ext-cust-2", new BigDecimal("1000.00"), "USD", pastDue, "ISSUED")
        );
    }
    
    private List<AccountingApiPaymentDto> fetchPayments() {
        return List.of(
            new AccountingApiPaymentDto("ext-pay-1", "ext-inv-1", new BigDecimal("200.00"), Instant.now().minusSeconds(86400)),
            new AccountingApiPaymentDto("ext-pay-2", "ext-inv-3", new BigDecimal("300.00"), Instant.now().minusSeconds(172800))
        );
    }
}
