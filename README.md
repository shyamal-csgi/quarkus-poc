# Quarkus POC

Standalone Quarkus **3.33 LTS** / **Java 25** sample service. It demonstrates patterns from the migration playbook: sync and async REST, Kafka producer and consumer, resilient outbound REST client, two Postgres datasources with Hibernate ORM, Caffeine caching, design-first OpenAPI, and a test pyramid with WireMock and k6.

Base package: `com.quarkus.poc.product`

## Tool details

| Tool | Version / note |
|------|----------------|
| JDK | **25** (Gradle toolchain + Foojay auto-provision; host JDK 21/24 is fine) |
| Build | Gradle **9.6.x** via `./gradlew` |
| Quarkus | **3.33.2** LTS BOM / plugin |
| Lombok | 1.18.38 (entities + `@Builder` on records) |
| Docker | Required for Testcontainers and local compose |
| k6 | Latest stable (`brew install k6`) for perf tasks |

## Quick start

```bash
# Infra (two Postgres + Kafka + WireMock)
docker compose -f docker-compose-local.yml up -d

# Dev mode
./gradlew quarkusDev

# Health / OpenAPI / Swagger UI
open http://localhost:8080/q/health
open http://localhost:8080/q/openapi
open http://localhost:8080/q/swagger-ui
```

## How to run tests

| Command | What it runs |
|---------|----------------|
| `./gradlew test` | **Unit** only (`unit.**`) - fast, no Docker |
| `./gradlew componentTest` | **Component** (`component.**`) - two Postgres Testcontainers |
| `./gradlew blackboxTest` | **Blackbox** (`blackbox.**`) - HTTP + WireMock + OpenAPI validation |
| `./gradlew ciTest` | unit + component + blackbox (PR gate) |
| `./gradlew integrationTest` | **Integration** (`integration.**`) - Testcontainers Kafka; not in `ciTest` |

Test design, tiers, and naming conventions: [docs/TESTING.md](docs/TESTING.md).

## Performance tests

See [performance-test/README.md](performance-test/README.md).

```bash
# Smoke (1 VU, 30s)
./gradlew perfSmoke -PbaseUrl=http://localhost:8080

# Load (default 10 VUs, 5 minutes) - override VUs / duration
./gradlew perfLoad -Pvus=50 -Pduration=5m -PbaseUrl=http://localhost:8080

# Or directly with k6
BASE_URL=http://localhost:8080 VUS=50 DURATION=5m k6 run performance-test/k6-load-test.js
```

## Endpoints

| Method | Path | Style |
|--------|------|-------|
| `GET` | `/api/v1/products` | Sync (virtual thread) |
| `GET` | `/api/v1/products/{id}` | Sync |
| `POST` | `/api/v1/products` | Sync + Kafka produce |
| `GET` | `/api/v1/products/{id}/profile` | **Async** `Uni` (product DB + supplier cache + enrichment client) |
| `GET` | `/api/v1/suppliers` | Sync (suppliers DB) |
| `GET` | `/api/v1/suppliers/{id}` | Sync + **Caffeine cache** |
| `GET` | `/q/health` | Health |
| `GET` | `/q/metrics` | Prometheus |
| `GET` | `/q/openapi` | OpenAPI |

Contract source of truth: [`src/main/resources/api/openapi.yaml`](src/main/resources/api/openapi.yaml).

## Design overview

```
Client
  │
  ▼
ProductResource / SupplierResource
  │
  ├── ProductService ──────────► products Postgres (Hibernate PU)
  │         │
  │         └── ProductEventProducer ──► Kafka topic products
  │
  ├── ProductProfileService (Uni)
  │         ├── SupplierCatalogService (@CacheResult) ──► suppliers Postgres
  │         └── EnrichmentClient (Uni + @Retry/@Timeout/@CircuitBreaker/@Fallback)
  │                   └── WireMock / external enrichment API
  │
  └── StockMovementEventConsumer ◄── Kafka topic stock-movements ──► products DB
```

Full architecture, decisions, and mermaid diagrams: [docs/DESIGN.md](docs/DESIGN.md).

Technology choices and dependency rationale: [docs/TECH-STACK.md](docs/TECH-STACK.md).

## WireMock

Shared stubs: [wiremock/README.md](wiremock/README.md). Blackbox tests embed WireMock via `WireMockTestResource`.

## Native / Docker

```bash
./gradlew build
docker build -f Dockerfile.jvm -t product-service:jvm .

# Native (container build; needs Docker)
docker build -f Dockerfile.native -t product-service:native .
```

## Configuration highlights

- Two datasources: `products` (port 5432) and `suppliers` (port 5433)
- Kafka: `quarkus.poc.signal.product.products` (out), `quarkus.poc.signal.product.stock-movements` (in)
- Enrichment: `ENRICHMENT_API_URL` (default `http://localhost:8089`)
- Tests use the SmallRye **in-memory** messaging connector (no broker for `ciTest`)
- Application config is YAML (`application.yml`); Gradle `gradle.properties` remains for build settings
