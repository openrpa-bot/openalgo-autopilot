package com.nigam.openalgo.autopilot.dblayer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity to store user configuration overrides from the UI.
 * These overrides have the highest priority in the configuration hierarchy.
 */
@Entity
@Table(name = "autopilot_configuration_override")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Configuration key (e.g., "common.host", "database.port")
     */
    @Column(name = "config_key", nullable = false, unique = true, length = 255)
    private String key;
    
    /**
     * Configuration value
     */
    @Column(name = "config_value", columnDefinition = "TEXT")
    private String value;
    
    /**
     * Description of what this configuration does
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * Configuration category (e.g., "database", "kafka", "openalgo")
     */
    @Column(name = "category", length = 100)
    private String category;
    
    /**
     * Whether this override is active
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    /**
     * Timestamp when this configuration was created
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when this configuration was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * User who created/updated this configuration
     */
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
