package com.takaada.integration.entities;

import jakarta.persistence.*;
import java.time.Instant;

// Stores the exact response of external API
@Entity
@Table(name = "raw_integration_data")
public class RawIntegrationData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String source;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "external_id", nullable = false)
    private String externalId;

    @Lob
    @Column(columnDefinition = "CLOB", nullable = false)
    private String payload;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public RawIntegrationData() {}

    public RawIntegrationData(String source, String entityType, String externalId, String payload) {
        this.source = source;
        this.entityType = entityType;
        this.externalId = externalId;
        this.payload = payload;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
