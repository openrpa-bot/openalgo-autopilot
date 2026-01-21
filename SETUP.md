# Setup Guide for Local Development

## Prerequisites

This application requires the following services to be running:

### 1. PostgreSQL Database (REQUIRED)
- **Port**: 5432
- **Database**: pgdb
- **Username**: pguser
- **Password**: pgpass

#### Setup PostgreSQL:
```sql
-- Connect to PostgreSQL as superuser
CREATE DATABASE pgdb;
CREATE USER pguser WITH PASSWORD 'pgpass';
GRANT ALL PRIVILEGES ON DATABASE pgdb TO pguser;
```

#### Using Docker:
```bash
docker run --name postgres-openalgo \
  -e POSTGRES_DB=pgdb \
  -e POSTGRES_USER=pguser \
  -e POSTGRES_PASSWORD=pgpass \
  -p 5432:5432 \
  -d postgres:15
```

### 2. Redis (REQUIRED for caching)
- **Port**: 6379

#### Using Docker:
```bash
docker run --name redis-openalgo \
  -p 6379:6379 \
  -d redis:7-alpine
```

### 3. Kafka (REQUIRED for message queue)
- **Port**: 9092

#### Using Docker Compose (recommended):
Create a `docker-compose.yml` file:
```yaml
version: '3.8'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

Then run:
```bash
docker-compose up -d
```

### 4. Temporal (Optional - for workflow orchestration)
- **Port**: 7233

## Running the Application

### Option 1: Using Gradle (with dev profile)
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Option 2: Build JAR and run
```bash
./gradlew clean build -x test
java -jar build/libs/openalgo-autopilot-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
```

### Option 3: Using Docker (after services are up)
```bash
docker-compose up -d  # Start all services
./gradlew bootRun
```

## Troubleshooting

### Application fails to start

1. **Check if PostgreSQL is running:**
   ```bash
   # Windows
   netstat -an | findstr 5432
   
   # Test connection
   psql -h localhost -U pguser -d pgdb
   ```

2. **Check if Redis is running:**
   ```bash
   # Windows
   netstat -an | findstr 6379
   
   # Test connection
   redis-cli ping
   ```

3. **Check if Kafka is running:**
   ```bash
   # Windows
   netstat -an | findstr 9092
   ```

4. **View application logs:**
   The application logs will show which service connection is failing. Look for errors like:
   - `Connection refused` - Service is not running
   - `Authentication failed` - Wrong credentials
   - `Database does not exist` - Database not created

### Common Issues

1. **Port already in use:**
   - Change the port in `application.properties` or stop the conflicting service

2. **Database connection timeout:**
   - Ensure PostgreSQL is running and accessible
   - Check firewall settings
   - Verify credentials in `application.properties`

3. **Redis connection failed:**
   - Ensure Redis is running
   - For development, you can temporarily switch to simple cache by changing `spring.cache.type=simple` in `application.properties`

4. **Kafka connection failed:**
   - Ensure Kafka and Zookeeper are running
   - Check that Kafka is listening on `localhost:9092`

## Quick Start with Docker Compose

Create a `docker-compose.yml` with all services:

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: pgdb
      POSTGRES_USER: pguser
      POSTGRES_PASSWORD: pgpass
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

volumes:
  postgres_data:
```

Run:
```bash
docker-compose up -d
./gradlew bootRun
```
