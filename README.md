# Sepiring Template

Backend REST API template built with **Kotlin** + **Spring Boot 4** + **Gradle (Kotlin DSL)**.

Includes JWT authentication, dual-database support, rate limiting, file storage (S3/SFTP/FTP), Flyway migrations, Swagger/OpenAPI docs, and Docker deployment — ready to be customized for your project.

## Features

- **JWT Authentication** — Register, login, and refresh token flow with role-based claims
- **Dual Database** — Primary datasource (items, users, files) + secondary datasource (audit logs) with separate `EntityManager` and `TransactionManager`
- **Rate Limiting** — IP-based (anonymous) and user-based (authenticated) via Bucket4j
- **File Storage** — Pluggable strategy pattern with S3, SFTP, and FTP implementations
- **Flyway Migrations** — Versioned SQL migrations for production (`V1` - `V4`)
- **Swagger / OpenAPI** — Auto-generated API docs at `/swagger-ui/index.html` (dev only)
- **Security Hardening** — XSS protection headers, CSP, HSTS, Jackson HTML character escaping
- **Request Logging** — Structured logging with MDC, JSON output in production (Logstash)
- **Global Exception Handler** — Unified error responses (400, 401, 403, 404, 500)
- **Docker** — Multi-stage build with non-root user + Docker Compose (PostgreSQL)

## Tech Stack

| Layer        | Technology                                |
| ------------ | ----------------------------------------- |
| Language     | Kotlin 2.2                                |
| Framework    | Spring Boot 4.0                           |
| Build Tool   | Gradle (Kotlin DSL)                       |
| Java         | JDK 21                                    |
| Database     | H2 (dev) / PostgreSQL 16 (prod)           |
| ORM          | Spring Data JPA / Hibernate               |
| Migration    | Flyway                                    |
| Security     | Spring Security + JWT (jjwt)              |
| Rate Limit   | Bucket4j                                  |
| API Docs     | SpringDoc OpenAPI (Swagger UI)            |
| File Storage | S3, SFTP (JSch), FTP (Commons Net)        |
| Logging      | Logback + Logstash Encoder                |
| Container    | Docker + Docker Compose                   |

## Requirements

- **JDK 21**
- **Gradle** (or use the included `gradlew` wrapper)
- **Docker** & **Docker Compose** (optional, for containerized deployment)

## Quick Start

### Development (H2 in-memory)

```bash
# 1. Clone the repo
git clone https://github.com/your-org/sepiring_template.git
cd sepiring_template/template

# 2. Setup environment variables
cp .env.dev .env

# 3. Run the app
.\gradlew bootRun
```

The app starts at **http://localhost:8080**. Root `/` redirects to Swagger UI.

### Using dev.ps1 (Recommended for dev)

The `dev.ps1` PowerShell script runs `bootRun` (foreground) + `build --continuous` (background) simultaneously, so you get hot-reload and auto-compilation:

```powershell
# Run dev mode (bootRun + continuous build)
.\dev.ps1

# Run build-only mode (continuous build)
.\dev.ps1 -Mode build-only
```

### Manual Two-Terminal Setup

For a better development experience, run two separate terminals so you get auto-compilation on file changes:

**Terminal 1 — Continuous Build** (auto-recompile on save):

```bash
cd template
.\gradlew build --continuous
```

**Terminal 2 — Run App** (hot-reload):

```bash
cd template
.\gradlew bootRun
```

Every time you save a file, Terminal 1 detects the change and recompiles, then Terminal 2 hot-reloads the app automatically.

### Docker Compose (PostgreSQL)

```bash
# Set required secrets
export JWT_SECRET=$(openssl rand -base64 32)
export AES_KEY_B64=$(openssl rand -base64 32)

# Build and run
cd template
docker compose up --build
```

This starts both PostgreSQL 16 and the app. Data is persisted in a Docker volume.

## Available Commands

| Command                       | Description                              |
| ----------------------------- | ---------------------------------------- |
| `.\gradlew bootRun`           | Start app in dev mode                    |
| `.\gradlew build`             | Compile, test, and package               |
| `.\gradlew build --continuous`| Auto-rebuild on file changes             |
| `.\gradlew test`              | Run unit and integration tests           |
| `.\gradlew bootJar`           | Build executable JAR                     |
| `.\dev.ps1`                   | Dev mode: bootRun + continuous build     |
| `.\dev.ps1 -Mode build-only`  | Continuous build only                    |
| `docker compose up --build`   | Build and run with Docker Compose        |

## Project Structure

```
template/
  build.gradle.kts            # Gradle build config
  dev.ps1                     # PowerShell dev helper
  Dockerfile                  # Multi-stage Docker build
  docker-compose.yml          # PostgreSQL + App services
  .env.example                # Environment variable reference
  .env.dev                    # Dev environment (H2)
  .env.prod                   # Production environment
  src/
    main/kotlin/com/sepring/template/
      TemplateApplication.kt      # Entry point
      config/                     # Security, OpenAPI, Jackson, exception handler
      controller/                 # REST controllers (Items, Auth, Files, Health)
      dto/                        # Pagination DTOs
      model/                      # JPA entities (Item, User, FileRecord)
      repository/                 # JPA repositories
      security/                   # JWT service, filters, AES encryption
      service/                    # Business logic + file storage implementations
      secondary/                  # Secondary datasource module (Audit Logs)
    main/resources/
      application.properties          # Main config (env-var driven)
      application-dev.properties      # Dev overrides (H2, Swagger enabled)
      application-prod.properties     # Prod overrides (PostgreSQL, Flyway)
      db/migration/                   # Flyway SQL migrations (V1 - V4)
    test/                             # Unit + integration tests
```

## API Endpoints

### Authentication (`/api/v1/auth`)

| Method | Endpoint      | Description             | Auth |
| ------ | ------------- | ----------------------- | ---- |
| POST   | `/register`   | Register new user       | No   |
| POST   | `/login`      | Login with credentials  | No   |
| POST   | `/refresh`    | Refresh access token    | No   |

### Items (`/api/v1/items`)

| Method | Endpoint      | Description             | Auth |
| ------ | ------------- | ----------------------- | ---- |
| GET    | `/`           | List items (paginated)  | Yes  |
| GET    | `/{id}`       | Get item by ID          | Yes  |
| POST   | `/`           | Create new item         | Yes  |
| PUT    | `/{id}`       | Update item             | Yes  |
| DELETE | `/{id}`       | Delete item             | Yes  |

### Files (`/api/v1/files`)

| Method | Endpoint      | Description             | Auth |
| ------ | ------------- | ----------------------- | ---- |
| POST   | `/upload`     | Upload file             | Yes  |
| GET    | `/{id}`       | Get file metadata       | Yes  |
| DELETE | `/{id}`       | Delete file             | Yes  |

### Audit Logs (`/api/v1/audit-logs`)

| Method | Endpoint      | Description             | Auth |
| ------ | ------------- | ----------------------- | ---- |
| GET    | `/`           | List all audit logs     | Yes  |
| POST   | `/`           | Create audit log entry  | Yes  |

### Health

| Method | Endpoint                    | Description                | Auth |
| ------ | --------------------------- | -------------------------- | ---- |
| GET    | `/api/v1/health`            | Health check (both DBs)    | No   |
| GET    | `/actuator/health`          | Spring Actuator health      | No   |

## Environment Variables

All configuration is driven by environment variables. See `.env.example` for the full reference.

| Variable                       | Default                       | Description                          |
| ------------------------------ | ----------------------------- | ------------------------------------ |
| `APP_NAME`                     | `template`                    | Application name                     |
| `SERVER_PORT`                  | `8080`                        | Server port                          |
| `SPRING_PROFILES_ACTIVE`      | `dev`                         | Spring profile (`dev` / `prod`)      |
| `DATABASE_URL`                 | `jdbc:h2:mem:template_db`     | Primary DB JDBC URL                  |
| `DATABASE_USERNAME`            | `sa`                          | Primary DB username                  |
| `DATABASE_PASSWORD`            |                               | Primary DB password                  |
| `DATABASE_DRIVER`              | `org.h2.Driver`               | Primary DB driver                    |
| `SECONDARY_DATABASE_URL`       | `jdbc:h2:mem:secondary_db`    | Secondary DB JDBC URL                |
| `SECONDARY_DATABASE_USERNAME`  | `sa`                          | Secondary DB username                |
| `SECONDARY_DATABASE_PASSWORD`  |                               | Secondary DB password                |
| `JWT_SECRET`                   | **(required)**                | Base64-encoded 256-bit HMAC key      |
| `JWT_EXPIRATION`               | `86400000` (24h)              | Access token lifetime (ms)           |
| `JWT_REFRESH_EXPIRATION`       | `604800000` (7d)              | Refresh token lifetime (ms)          |
| `AES_KEY_B64`                  | **(required)**                | Base64-encoded 256-bit AES key       |
| `RATE_LIMIT_PUBLIC_CAPACITY`   | `100`                         | Rate limit for anonymous users       |
| `RATE_LIMIT_AUTH_CAPACITY`     | `60`                          | Rate limit for authenticated users   |
| `RATE_LIMIT_RESET_SECONDS`     | `60`                          | Rate limit window (seconds)          |
| `FILE_STORAGE_TYPE`            | `s3`                          | Storage: `s3`, `sftp`, or `ftp`      |
| `FLYWAY_ENABLED`               | `false`                       | Enable Flyway (set `true` in prod)   |
| `LOG_LEVEL_ROOT`               | `INFO`                        | Root log level                       |
| `LOG_LEVEL_APP`                | `DEBUG`                       | Application log level                |

> **Required secrets:** `JWT_SECRET` and `AES_KEY_B64` must be set before startup. Generate with:
> ```bash
> openssl rand -base64 32
> ```

## Testing

```bash
# Run all tests
.\gradlew test
```

Tests use H2 in-memory database for both primary and secondary datasources. Test JWT/AES keys are configured in `src/test/resources/application.properties`.

| Test                          | Type        | Description                       |
| ----------------------------- | ----------- | --------------------------------- |
| `TemplateApplicationTests`    | Integration | Context loads                     |
| `ItemServiceTest`             | Unit        | Item CRUD logic (mocked repo)     |
| `JwtServiceTest`              | Unit        | JWT generation & validation       |
| `AesEcbUtilTest`              | Unit        | AES-CBC encrypt/decrypt           |
| `ItemRepositoryTest`          | Repository  | JPA repository operations         |
| `ItemControllerTest`          | Integration | REST controller layer             |
| `JacksonHtmlEscapeTest`       | Unit        | HTML escaping in JSON output      |
| `HealthIntegrationTest`       | Integration | Dual database connectivity        |

## License

MIT
