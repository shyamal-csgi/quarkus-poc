# WireMock stubs (enrichment)

Shared stub mappings for local development and blackbox-style manual runs.

## Layout

```
wiremock/
  enrichment/
    mappings/
      happy-200-enrichment.json
      error-503-service-unavailable.json
      timeout-delay-5s.json
    __files/
  docker-compose.wiremock.yml
```

Naming follows the migration playbook: `happy-200-<variant>.json`, `error-<status>-<description>.json`, `timeout-delay-<n>s.json`.

## Run standalone

```bash
docker compose -f wiremock/docker-compose.wiremock.yml up
```

Enrichment base URL: `http://localhost:8089`

Point the app at it:

```bash
export ENRICHMENT_API_URL=http://localhost:8089
./gradlew quarkusDev
```

Blackbox tests start an embedded WireMock via `WireMockTestResource` and do not require this compose file.
