# OpenAlgo Autopilot

A multi-module Spring Boot application with three main modules:
- **UI Module**: Thymeleaf-based web interface
- **Socket Listener Module**: OpenAlgo socket connection handler
- **API Module**: REST API endpoints

## Project Structure

```
openalgo-autopilot/
├── ui-module/              # Thymeleaf UI (Port 8080)
├── socket-listener-module/ # OpenAlgo Socket Listener (Port 8081)
├── api-module/             # REST API (Port 8082)
├── build.gradle            # Root build configuration
├── settings.gradle         # Gradle module settings
└── gradle.properties       # Version properties
```

## Prerequisites

- Java 17 or higher
- Gradle 8.10.2 (wrapper included)

## Building the Project

```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :ui-module:build
./gradlew :socket-listener-module:build
./gradlew :api-module:build
```

## Running the Modules

### UI Module (Port 8080)
```bash
./gradlew :ui-module:bootRun
```
Access at: http://localhost:8080

### Socket Listener Module (Port 8081)
```bash
./gradlew :socket-listener-module:bootRun
```

### API Module (Port 8082)
```bash
./gradlew :api-module:bootRun
```
Access at: http://localhost:8082
- API Docs: http://localhost:8082/swagger-ui.html
- Health Check: http://localhost:8082/api/v1/health

## Dependencies

The project includes:
- Spring Boot 3.4.1
- Thymeleaf (UI module)
- Spring Data REST
- Spring Data JPA
- MySQL Connector
- Redis/Jedis
- Liquibase
- Quartz Scheduler
- Temporal SDK
- OpenAPI/Swagger
- Prometheus metrics
- TA4J (Technical Analysis)

## Configuration

Each module has its own `application.yml` file for configuration:
- Database connection settings
- Redis configuration
- OpenAlgo socket settings (in socket-listener-module)

## Development

The project uses Lombok for reducing boilerplate code. Make sure your IDE has Lombok plugin installed.

## License

[Add your license here]
