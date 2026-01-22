package com.nigam.openalgo.autopilot.configuration.service;

import com.nigam.openalgo.autopilot.dblayer.entity.ConfigurationEntity;
import com.nigam.openalgo.autopilot.dblayer.repository.ConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Configuration Service that implements priority hierarchy:
 * 1. Environment Variables (highest priority)
 * 2. System Properties
 * 3. application.properties
 * 4. Redis Configuration
 * 5. Database/UI Overrides (highest priority - user overrides)
 * 
 * The service provides a unified way to access configuration values
 * with the correct priority order.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigurationService {
    
    private final Environment environment;
    private final ConfigurationRepository configurationRepository;
    private final StringRedisTemplate redisTemplate;
    private final ResourceLoader resourceLoader;
    
    // Redis key prefix for configuration
    private static final String REDIS_CONFIG_PREFIX = "config:";
    
    /**
     * Get configuration value with priority hierarchy:
     * 1. UI/Database Override (highest)
     * 2. Redis Configuration
     * 3. Environment Variable
     * 4. System Property
     * 5. application.properties
     * 
     * @param key Configuration key
     * @return Configuration value or null if not found
     */
    public String getValue(String key) {
        return getValue(key, null);
    }
    
    /**
     * Get configuration value with default
     * 
     * @param key Configuration key
     * @param defaultValue Default value if not found
     * @return Configuration value or default
     */
    public String getValue(String key, String defaultValue) {
        return getValue(key, defaultValue, false);
    }
    
    /**
     * Get configuration value with default and recursion guard
     * 
     * @param key Configuration key
     * @param defaultValue Default value if not found
     * @param skipEnvironment Skip environment lookup to prevent circular dependency
     * @return Configuration value or default
     */
    public String getValue(String key, String defaultValue, boolean skipEnvironment) {
        // Priority 1: UI/Database Override (highest priority)
        try {
            Optional<ConfigurationEntity> override = configurationRepository.findByKeyAndIsActiveTrue(key);
            if (override.isPresent()) {
                log.debug("Configuration '{}' found in database override: {}", key, override.get().getValue());
                return override.get().getValue();
            }
        } catch (Exception e) {
            log.warn("Error reading database override for '{}': {}", key, e.getMessage());
        }
        
        // Priority 2: Redis Configuration
        try {
            String redisKey = REDIS_CONFIG_PREFIX + key;
            String redisValue = redisTemplate.opsForValue().get(redisKey);
            if (redisValue != null && !redisValue.isEmpty()) {
                log.debug("Configuration '{}' found in Redis: {}", key, redisValue);
                return redisValue;
            }
        } catch (Exception e) {
            log.warn("Error reading Redis config for '{}': {}", key, e.getMessage());
        }
        
        // Priority 3: Environment Variable
        String envValue = System.getenv(key.replace(".", "_").toUpperCase());
        if (envValue != null && !envValue.isEmpty()) {
            log.debug("Configuration '{}' found in environment variable: {}", key, envValue);
            return envValue;
        }
        
        // Priority 4: System Property
        String sysPropValue = System.getProperty(key);
        if (sysPropValue != null && !sysPropValue.isEmpty()) {
            log.debug("Configuration '{}' found in system property: {}", key, sysPropValue);
            return sysPropValue;
        }
        
        // Priority 5: application.properties (via Spring Environment)
        // Skip if called from PropertySource to prevent circular dependency
        if (!skipEnvironment) {
            try {
                String propValue = environment.getProperty(key);
                if (propValue != null && !propValue.isEmpty()) {
                    log.debug("Configuration '{}' found in application.properties: {}", key, propValue);
                    return propValue;
                }
            } catch (Exception e) {
                log.warn("Error reading property '{}' from environment: {}", key, e.getMessage());
            }
        }
        
        log.debug("Configuration '{}' not found, using default: {}", key, defaultValue);
        return defaultValue;
    }
    
    /**
     * Get all configuration values for a category
     */
    public Map<String, String> getValuesByCategory(String category) {
        Map<String, String> configs = new HashMap<>();
        
        // Get from database overrides
        List<ConfigurationEntity> overrides = configurationRepository.findByCategoryAndIsActiveTrue(category);
        for (ConfigurationEntity override : overrides) {
            configs.put(override.getKey(), override.getValue());
        }
        
        // Get from Redis (only if not overridden)
        Set<String> redisKeys = redisTemplate.keys(REDIS_CONFIG_PREFIX + category + ":*");
        if (redisKeys != null) {
            for (String redisKey : redisKeys) {
                String key = redisKey.substring(REDIS_CONFIG_PREFIX.length());
                if (!configs.containsKey(key)) {
                    String value = redisTemplate.opsForValue().get(redisKey);
                    if (value != null) {
                        configs.put(key, value);
                    }
                }
            }
        }
        
        return configs;
    }
    
    /**
     * Get all configuration keys with their sources
     */
    public Map<String, ConfigurationSource> getAllConfigurations() {
        Map<String, ConfigurationSource> allConfigs = new HashMap<>();
        
        // Step 1: Get all from application.properties first (lowest priority, so we can override)
        try {
            // Read application.properties file directly
            Resource resource = resourceLoader.getResource("classpath:application.properties");
            if (resource.exists()) {
                Properties props = new Properties();
                try (InputStream inputStream = resource.getInputStream()) {
                    props.load(inputStream);
                    for (String key : props.stringPropertyNames()) {
                        // Only add if not already present (UI/Redis have higher priority)
                        if (!allConfigs.containsKey(key)) {
                            String value = props.getProperty(key);
                            // Show all non-empty properties from application.properties
                            // For placeholders, show the raw value (users can see and override if needed)
                            if (value != null && !value.trim().isEmpty()) {
                                allConfigs.put(key, new ConfigurationSource(
                                    value,
                                    ConfigurationPriority.PROPERTIES_FILE,
                                    extractCategory(key),
                                    null
                                ));
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.warn("Error reading application.properties: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("Error loading application.properties resource: {}", e.getMessage());
        }
        
        // Step 2: Get from Redis (only if not already present)
        try {
            Set<String> redisKeys = redisTemplate.keys(REDIS_CONFIG_PREFIX + "*");
            if (redisKeys != null) {
                for (String redisKey : redisKeys) {
                    String key = redisKey.substring(REDIS_CONFIG_PREFIX.length());
                    if (!allConfigs.containsKey(key)) {
                        String value = redisTemplate.opsForValue().get(redisKey);
                        if (value != null) {
                            allConfigs.put(key, new ConfigurationSource(
                                value,
                                ConfigurationPriority.REDIS,
                                extractCategory(key),
                                null
                            ));
                        }
                    } else {
                        // Update existing entry to Redis if it was from properties file
                        ConfigurationSource existing = allConfigs.get(key);
                        if (existing.getPriority() == ConfigurationPriority.PROPERTIES_FILE) {
                            String value = redisTemplate.opsForValue().get(redisKey);
                            if (value != null) {
                                allConfigs.put(key, new ConfigurationSource(
                                    value,
                                    ConfigurationPriority.REDIS,
                                    extractCategory(key),
                                    null
                                ));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error reading from Redis: {}", e.getMessage());
        }
        
        // Step 3: Get all from database overrides (highest priority, so they override everything)
        try {
            List<ConfigurationEntity> overrides = configurationRepository.findByIsActiveTrue();
            for (ConfigurationEntity override : overrides) {
                allConfigs.put(override.getKey(), new ConfigurationSource(
                    override.getValue(),
                    ConfigurationPriority.UI_OVERRIDE,
                    override.getCategory(),
                    override.getDescription()
                ));
            }
        } catch (Exception e) {
            log.warn("Error reading database overrides: {}", e.getMessage());
        }
        
        return allConfigs;
    }
    
    /**
     * Save or update UI configuration override
     */
    @Transactional
    public ConfigurationEntity saveOverride(String key, String value, String category, String description, String updatedBy) {
        Optional<ConfigurationEntity> existing = configurationRepository.findByKey(key);
        
        ConfigurationEntity entity;
        if (existing.isPresent()) {
            entity = existing.get();
            entity.setValue(value);
            entity.setCategory(category);
            entity.setDescription(description);
            entity.setUpdatedBy(updatedBy);
            entity.setIsActive(true);
        } else {
            entity = ConfigurationEntity.builder()
                    .key(key)
                    .value(value)
                    .category(category)
                    .description(description)
                    .updatedBy(updatedBy)
                    .isActive(true)
                    .build();
        }
        
        ConfigurationEntity saved = configurationRepository.save(entity);
        log.info("Configuration override saved: {} = {} (by {})", key, value, updatedBy);
        return saved;
    }
    
    /**
     * Delete configuration override (soft delete by setting isActive = false)
     */
    @Transactional
    public void deleteOverride(String key) {
        Optional<ConfigurationEntity> existing = configurationRepository.findByKey(key);
        if (existing.isPresent()) {
            ConfigurationEntity entity = existing.get();
            entity.setIsActive(false);
            configurationRepository.save(entity);
            log.info("Configuration override deleted: {}", key);
        }
    }
    
    /**
     * Delete all configuration overrides (reset all to default values from application.properties)
     */
    @Transactional
    public int deleteAllOverrides() {
        List<ConfigurationEntity> allOverrides = configurationRepository.findByIsActiveTrue();
        int count = 0;
        for (ConfigurationEntity entity : allOverrides) {
            entity.setIsActive(false);
            configurationRepository.save(entity);
            count++;
        }
        log.info("Deleted {} configuration overrides - all configurations reset to defaults", count);
        return count;
    }
    
    /**
     * Save configuration to Redis
     */
    public void saveToRedis(String key, String value) {
        String redisKey = REDIS_CONFIG_PREFIX + key;
        redisTemplate.opsForValue().set(redisKey, value);
        log.info("Configuration saved to Redis: {} = {}", key, value);
    }
    
    /**
     * Delete configuration from Redis
     */
    public void deleteFromRedis(String key) {
        String redisKey = REDIS_CONFIG_PREFIX + key;
        redisTemplate.delete(redisKey);
        log.info("Configuration deleted from Redis: {}", key);
    }
    
    /**
     * Get all categories
     */
    public List<String> getAllCategories() {
        List<String> categories = configurationRepository.findDistinctCategories();
        if (categories == null || categories.isEmpty()) {
            return Arrays.asList("database", "kafka", "openalgo", "redis", "temporal", "server", "common");
        }
        return categories;
    }
    
    /**
     * Get count of active configuration overrides
     */
    public long getOverrideCount() {
        try {
            return configurationRepository.findByIsActiveTrue().size();
        } catch (Exception e) {
            log.warn("Error counting overrides: {}", e.getMessage());
            return 0;
        }
    }
    
    private String extractCategory(String key) {
        if (key.contains(".")) {
            return key.substring(0, key.indexOf("."));
        }
        return "general";
    }
    
    /**
     * Configuration source information
     */
    public static class ConfigurationSource {
        private final String value;
        private final ConfigurationPriority priority;
        private final String category;
        private final String description;
        
        public ConfigurationSource(String value, ConfigurationPriority priority, String category, String description) {
            this.value = value;
            this.priority = priority;
            this.category = category;
            this.description = description;
        }
        
        public String getValue() { return value; }
        public ConfigurationPriority getPriority() { return priority; }
        public String getCategory() { return category; }
        public String getDescription() { return description; }
    }
    
    /**
     * Configuration priority levels
     */
    public enum ConfigurationPriority {
        UI_OVERRIDE,    // Highest priority - user overrides from UI
        REDIS,          // Redis configuration
        ENVIRONMENT,    // Environment variables
        SYSTEM_PROPERTY,// System properties
        PROPERTIES_FILE // application.properties (lowest priority)
    }
}
