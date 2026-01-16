package com.nigam.openalgo.autopilot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class TablePrefixConfiguration {
    
    @Value("${app.table.prefix:OA_}")
    private String tablePrefix;
    
    @PostConstruct
    public void setSystemProperty() {
        // Set system property so PrefixedPhysicalNamingStrategy can read it
        System.setProperty("app.table.prefix", tablePrefix);
    }
}
