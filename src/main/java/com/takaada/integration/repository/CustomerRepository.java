package com.takaada.integration.repository;

import com.takaada.integration.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findBySourceAndExternalId(String source, String externalId);
}
