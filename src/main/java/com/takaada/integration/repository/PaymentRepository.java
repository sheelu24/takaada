package com.takaada.integration.repository;

import com.takaada.integration.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByInvoiceId(Long invoiceId);

    java.util.Optional<Payment> findBySourceAndExternalId(String source, String externalId);
}
