# Configuration Module

A comprehensive configuration management system with priority hierarchy and UI-based overrides.

## Features

- **Priority Hierarchy**: Configuration values are resolved in the following order (highest to lowest priority):
  1. **UI/Database Overrides** - User-defined overrides from the web UI (highest priority)
  2. **Redis Configuration** - Runtime configuration stored in Redis
  3. **Environment Variables** - System environment variables
  4. **System Properties** - Java system properties
  5. **application.properties** - Default configuration file (lowest priority)

- **Web UI**: Thymeleaf-based interface for managing configuration overrides
- **Spring Integration**: Seamless integration with Spring's `@Value` and `@ConfigurationProperties`
- **Category Management**: Organize configurations by category (database, kafka, openalgo, etc.)
- **Audit Trail**: Track who created/updated configurations and when

## Usage

### Accessing Configuration Values

#### Using ConfigurationService (Recommended)
```java
@Autowired
private ConfigurationService configurationService;

String dbHost = configurationService.getValue("database.host");
String dbPort = configurationService.getValue("database.port", "5432"); // with default
```

#### Using @Value Annotation
```java
@Value("${database.host}")
private String databaseHost;

@Value("${database.port:5432}") // with default
private String databasePort;
```

#### Using @ConfigurationProperties
```java
@ConfigurationProperties(prefix = "database")
public class DatabaseConfig {
    private String host;
    private int port;
    // getters and setters
}
```

### Managing Configuration via UI

1. Navigate to `/configuration` in your browser
2. View all configurations grouped by category
3. Click "Edit" to modify a configuration
4. Click "Add New Configuration" to create an override
5. UI overrides will take effect immediately (may require application restart for some properties)

### Programmatic Configuration Management

```java
@Autowired
private ConfigurationService configurationService;

// Save UI override
configurationService.saveOverride(
    "database.host",
    "new-host.example.com",
    "database",
    "Database server hostname",
    "admin"
);

// Save to Redis
configurationService.saveToRedis("kafka.bootstrap-servers", "localhost:9092");

// Delete override
configurationService.deleteOverride("database.host");
```

## API Endpoints

- `GET /configuration` - List all configurations
- `GET /configuration?category=database` - Filter by category
- `GET /configuration/edit?key=database.host` - Edit existing configuration
- `GET /configuration/edit` - Create new configuration
- `POST /configuration/save` - Save configuration override
- `POST /configuration/delete` - Delete configuration override
- `GET /configuration/value/{key}` - Get configuration value (JSON)

## Database Schema

The module creates a table `autopilot_configuration_override` with the following structure:

- `id` - Primary key
- `config_key` - Configuration key (unique)
- `config_value` - Configuration value
- `category` - Configuration category
- `description` - Description of the configuration
- `is_active` - Whether the override is active
- `created_at` - Creation timestamp
- `updated_at` - Last update timestamp
- `updated_by` - User who made the change

## Configuration Priority Examples

### Example 1: Database Host Configuration

1. **application.properties**: `database.host=localhost`
2. **Environment Variable**: `DATABASE_HOST=prod-db.example.com`
3. **Redis**: `config:database.host=staging-db.example.com`
4. **UI Override**: `database.host=custom-db.example.com`

**Result**: `custom-db.example.com` (UI override wins)

### Example 2: Kafka Bootstrap Servers

1. **application.properties**: `spring.kafka.bootstrap-servers=localhost:9092`
2. **Redis**: `config:spring.kafka.bootstrap-servers=staging-kafka:9092`
3. **No UI Override**

**Result**: `staging-kafka:9092` (Redis wins)

## Best Practices

1. **Use UI Overrides for**: Environment-specific values that change frequently
2. **Use Redis for**: Runtime configuration that needs to be shared across instances
3. **Use Environment Variables for**: Sensitive data (passwords, API keys) - never store in UI
4. **Use application.properties for**: Default values and development configuration

## Security Considerations

- Configuration values are stored in plain text in the database
- Sensitive values (passwords, API keys) should use environment variables or secure vaults
- Consider implementing authentication/authorization for the configuration UI
- Audit trail tracks who made changes for compliance

## Integration with Spring Boot

The module automatically registers a custom `PropertySource` that integrates with Spring's configuration system. This means:

- `@Value` annotations work seamlessly
- `@ConfigurationProperties` classes receive values from the configuration service
- Configuration changes can be applied without code changes
- Hot-reload support for certain configuration types
