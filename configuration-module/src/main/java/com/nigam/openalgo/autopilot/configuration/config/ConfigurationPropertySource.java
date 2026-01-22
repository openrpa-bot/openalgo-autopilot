package com.nigam.openalgo.autopilot.configuration.config;

import com.nigam.openalgo.autopilot.configuration.service.ConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Custom PropertySource that integrates ConfigurationService with Spring's Environment.
 * This allows @Value annotations and @ConfigurationProperties to use values from
 * the configuration service with proper priority hierarchy.
 */
@Component
@Slf4j
public class ConfigurationPropertySource extends PropertySource<ConfigurationService> {
    
    private final ConfigurationService configurationService;
    
    public ConfigurationPropertySource(ConfigurationService configurationService) {
        super("configurationService", configurationService);
        this.configurationService = configurationService;
    }
    
    @PostConstruct
    public void init() {
        log.info("ConfigurationPropertySource initialized - UI overrides will have highest priority");
    }
    
    @Override
    public Object getProperty(String name) {
        try {
            // Only return UI/Redis overrides to prevent circular dependency
            // Skip environment lookup (skipEnvironment=true) to break the cycle
            // This ensures PropertySource only provides overrides, not full resolution
            String value = configurationService.getValue(name, null, true);
            if (value != null) {
                log.trace("Property '{}' resolved from ConfigurationService override: {}", name, value);
                return value;
            }
        } catch (StackOverflowError e) {
            log.error("StackOverflowError detected - circular dependency in property resolution for '{}'", name);
            return null;
        } catch (Exception e) {
            // Silently ignore errors during early startup (database might not be ready)
            log.trace("Error getting property '{}' from ConfigurationService (may be during startup): {}", name, e.getMessage());
        }
        return null;
    }
    
    @Override
    public boolean containsProperty(String name) {
        try {
            // Only check UI/Redis overrides to prevent circular dependency
            String value = configurationService.getValue(name, null, true);
            return value != null;
        } catch (StackOverflowError e) {
            log.error("StackOverflowError detected in containsProperty for '{}'", name);
            return false;
        } catch (Exception e) {
            // Silently ignore errors during early startup
            return false;
        }
    }
}
