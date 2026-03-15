package com.takaada.integration.repository;

import com.takaada.integration.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByCustomerId(Long customerId);

    java.util.Optional<Invoice> findBySourceAndExternalId(String source, String externalId);

    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :today AND i.status != 'PAID'")
    List<Invoice> findOverdueInvoices(LocalDate today);
}
