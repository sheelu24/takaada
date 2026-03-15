package com.takaada.integration.controller;

import com.takaada.integration.service.InsightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/insights")
public class InsightController {

    private final InsightService insightService;

    public InsightController(InsightService insightService) {
        this.insightService = insightService;
    }

    //Outstanding balance for a customer
    @GetMapping("/customers/{customerId}/outstanding-balance")
    public ResponseEntity<Map<String, Object>> getOutstandingBalance(@PathVariable Long customerId) {
        BigDecimal balance = insightService.getOutstandingBalanceForCustomer(customerId);
        return ResponseEntity.ok(Map.of(
            "customerId", customerId,
            "outstandingBalance", balance,
            "currency", "USD"
        ));
    }

    //List of overdue invoices
    @GetMapping("/invoices/overdue")
    public ResponseEntity<List<InsightService.OverdueInvoiceDto>> getOverdueInvoices() {
        return ResponseEntity.ok(insightService.getOverdueInvoices());
    }
}
