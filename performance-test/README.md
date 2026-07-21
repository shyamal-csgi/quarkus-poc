# Performance tests (k6)

## Scripts

| Script | Purpose | Default |
|--------|---------|---------|
| `k6-smoke-test.js` | Sanity: health + top endpoints | 1 VU, 30s |
| `k6-load-test.js` | Load / soak | 10 VUs, **5m** |

Results under `results/` are gitignored.

## Parameters

| Env / Gradle `-P` | Meaning | Default |
|-------------------|---------|---------|
| `BASE_URL` / `-PbaseUrl` | SUT base URL | `http://localhost:8080` |
| `VUS` / `-Pvus` | Virtual users (load only) | `10` |
| `DURATION` / `-Pduration` | Duration (load only) | `5m` |

## Run

Start the app and dependencies first (`docker compose -f docker-compose-local.yml up -d`, then `./gradlew quarkusDev`).

### Smoke

```bash
BASE_URL=http://localhost:8080 k6 run performance-test/k6-smoke-test.js
# or
./gradlew perfSmoke -PbaseUrl=http://localhost:8080
```

### Load (5 minutes by default)

```bash
BASE_URL=http://localhost:8080 VUS=50 DURATION=5m k6 run performance-test/k6-load-test.js
# or
./gradlew perfLoad -Pvus=50 -Pduration=5m -PbaseUrl=http://localhost:8080
```

### Shorter local dry-run

```bash
./gradlew perfLoad -Pvus=2 -Pduration=30s
```
