package com.nigam.openalgo.autopilot.configuration.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

/**
 * Configuration class to register the custom PropertySource with Spring Environment.
 * This ensures that ConfigurationService values are available to @Value and @ConfigurationProperties.
 */
@Configuration
@Slf4j
public class ConfigurationPropertySourceConfig {
    
    private final ConfigurableEnvironment environment;
    private final ConfigurationPropertySource configurationPropertySource;
    
    public ConfigurationPropertySourceConfig(
            ConfigurableEnvironment environment,
            ConfigurationPropertySource configurationPropertySource) {
        this.environment = environment;
        this.configurationPropertySource = configurationPropertySource;
    }
    
    /**
     * Register the custom PropertySource early in the property source chain
     * so it has high priority (after system properties and environment variables,
     * but before application.properties)
     */
    @Bean
    public ConfigurationPropertySource registerPropertySource() {
        MutablePropertySources propertySources = environment.getPropertySources();
        
        // Add after system properties but before application.properties
        // This gives it priority over application.properties but respects system/env vars
        if (propertySources.contains("systemProperties")) {
            propertySources.addAfter("systemProperties", configurationPropertySource);
            log.info("ConfigurationPropertySource registered after systemProperties");
        } else {
            propertySources.addFirst(configurationPropertySource);
            log.info("ConfigurationPropertySource registered as first property source");
        }
        
        return configurationPropertySource;
    }
}
