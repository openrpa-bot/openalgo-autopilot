package com.nigam.openalgo.autopilot.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class PrefixedPhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {
    
    // Read from system property or use default
    private static final String TABLE_PREFIX = System.getProperty("app.table.prefix", 
            System.getenv().getOrDefault("APP_TABLE_PREFIX", "OA_"));
    
    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        String tableName = name.getText();
        
        // Skip prefix if table already has the prefix
        if (tableName.startsWith(TABLE_PREFIX)) {
            return super.toPhysicalTableName(name, context);
        }
        
        // Add prefix to all tables
        tableName = TABLE_PREFIX + tableName;
        
        return Identifier.toIdentifier(tableName, name.isQuoted());
    }
}
