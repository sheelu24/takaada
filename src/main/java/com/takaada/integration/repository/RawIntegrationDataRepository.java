package com.takaada.integration.repository;

import com.takaada.integration.entities.RawIntegrationData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RawIntegrationDataRepository extends JpaRepository<RawIntegrationData, Long> {

    List<RawIntegrationData> findBySourceAndEntityTypeOrderByCreatedAtDesc(String source, String entityType);
}
