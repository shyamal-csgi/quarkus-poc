# Technology Stack

This document describes each dependency in `build.gradle.kts`, why it was chosen, and the overall technical approach for the product service.

## Runtime and build

| Dependency | Rationale |
|------------|-----------|
| **Quarkus 3.33 LTS** (BOM) | LTS stream with Java 25 support; fast startup, low memory, native-image option |
| **Gradle + Foojay toolchain** | Reproducible JDK 25 provisioning; task hierarchy for test pyramid (`ciTest`, perf tasks) |
| **Lombok** | Reduces boilerplate on entities (`@Getter`/`@Setter`) and DTO records (`@Builder`) |
| **quarkus-config-yaml** | Nested YAML for dual datasources, Kafka channels, and `%test` profile overrides |

## HTTP layer

| Dependency | Rationale |
|------------|-----------|
| **quarkus-rest** | Jakarta REST (JAX-RS) endpoints; virtual threads via `@RunOnVirtualThread` for sync handlers |
| **quarkus-rest-jackson** | JSON serialisation for request/response bodies |
| **quarkus-hibernate-validator** | Bean Validation on DTOs (`@NotBlank`, etc.) |
| **quarkus-smallrye-openapi** | Serves design-first contract from `api/openapi.yaml` |
| **quarkus-smallrye-health** | Liveness/readiness at `/q/health` |
| **quarkus-micrometer-registry-prometheus** | Metrics export at `/q/metrics` |

## Persistence

| Dependency | Rationale |
|------------|-----------|
| **quarkus-hibernate-orm** | JPA entities with named persistence units (`products`, `suppliers`) |
| **quarkus-jdbc-postgresql** + **quarkus-agroal** | Connection pooling for two independent Postgres databases |
| **quarkus-flyway** | Schema migrations per datasource (`db/products`, `db/suppliers`) |

## Messaging

| Dependency | Rationale |
|------------|-----------|
| **quarkus-messaging-kafka** | Kafka producer (`product-events`) and consumer (`stock-events`) via MicroProfile Reactive Messaging |

## Resilience and integration

| Dependency | Rationale |
|------------|-----------|
| **quarkus-rest-client** + **quarkus-rest-client-jackson** | Declarative outbound HTTP to enrichment API |
| **quarkus-smallrye-fault-tolerance** | `@Retry`, `@Timeout`, `@CircuitBreaker`, `@Fallback` on the enrichment client |
| **quarkus-cache** (Caffeine) | In-process cache for supplier lookups (`@CacheResult`) |

## Test dependencies

| Dependency | Rationale |
|------------|-----------|
| **quarkus-junit5** + **quarkus-junit5-mockito** | Quarkus test lifecycle with CDI and Mockito |
| **rest-assured** | Fluent HTTP assertions in blackbox tests |
| **assertj-core** | Readable assertions in unit and integration tests |
| **testcontainers** (postgres, kafka, junit-jupiter) | Real infrastructure in component, blackbox, and integration tiers |
| **wiremock-standalone** | Embedded stubs for enrichment API in blackbox tests |
| **swagger-request-validator-restassured** | Validates HTTP responses against `openapi.yaml` |
| **awaitility** | Polls for async Kafka consume in integration tests |
| **smallrye-reactive-messaging-in-memory** | In-memory connector so `ciTest` needs no Kafka broker |

## Technical approach

### Layering (hexagonal-ish)

```
api/          REST resources (inbound adapters)
service/      Domain orchestration
persistence/  JPA repositories (outbound adapters)
kafka/        Messaging producers/consumers
client/       Outbound REST client
dto/          API and event contracts
domain/       JPA entities per datasource
config/       `@ConfigMapping` for `product.*` settings
exception/    Sealed not-found hierarchy + JAX-RS mappers
```

Resources stay thin; services own business logic; repositories encapsulate persistence.

### Dual datasources

- **products** (`qp_products`, port 5432): write model (`Product`, `StockMovement`)
- **suppliers** (`qp_suppliers`, port 5433): read/reference model (`Supplier`, `SupplierProduct`)

Each has its own Agroal pool, Hibernate persistence unit, and Flyway migration location.

### Sync vs async

- Sync CRUD uses virtual threads (`@RunOnVirtualThread`) for blocking JPA without platform thread exhaustion.
- Profile aggregation returns `Uni<ProductProfileResponse>`: combines supplier cache lookup and enrichment client on worker pools.

### Resilience patterns

The enrichment client demonstrates production-style outbound integration:

- Timeout (2s) and retry (2 attempts, 200ms delay)
- Circuit breaker (50% failure ratio after 4 requests, 5s delay)
- Fallback returning a degraded response when the upstream is unavailable

### Caching

`SupplierCatalogService.getById` is annotated with `@CacheResult(cacheName = "suppliers")`. Caffeine TTL and size are configured in `application.yml`.

### Design-first OpenAPI

`src/main/resources/api/openapi.yaml` is the contract source of truth. Blackbox tests validate every response against it using the Atlassian OpenAPI validator.

### Fowler test pyramid

| Tier | Scope | Infrastructure |
|------|-------|----------------|
| Unit | Services, mappers, fallback | None (Mockito) |
| Component | Repositories + Flyway | Two Postgres Testcontainers |
| Blackbox | Full HTTP stack | Postgres + embedded WireMock |
| Integration | Kafka consume path | Postgres + Kafka Testcontainers |

`ciTest` runs unit + component + blackbox. Integration tests are opt-in (`./gradlew integrationTest`).

### YAML configuration

Quarkus application config lives in `application.yml` (main and test). Gradle `gradle.properties` remains for build/plugin versions only.

Environment variable prefixes:

- `PRODUCT_PRODUCTS_DB_*` for the products datasource
- `PRODUCT_SUPPLIERS_DB_*` for the suppliers datasource
- `KAFKA_BOOTSTRAP_SERVERS`, `ENRICHMENT_API_URL` for external services
