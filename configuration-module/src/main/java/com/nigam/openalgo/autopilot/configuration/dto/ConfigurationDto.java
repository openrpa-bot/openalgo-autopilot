package com.nigam.openalgo.autopilot.configuration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for configuration display and editing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationDto {
    private Long id;
    private String key;
    private String value;
    private String description;
    private String category;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private String source; // UI_OVERRIDE, REDIS, ENVIRONMENT, PROPERTIES_FILE
    private String currentValue; // Current effective value from all sources
}
