import http from 'k6/http';
import { check, sleep } from 'k6';

/**
 * Load test. Defaults: 10 VUs for 5 minutes.
 *
 * Override via env or Gradle -P props:
 *   BASE_URL=http://localhost:8080 VUS=50 DURATION=5m k6 run performance-test/k6-load-test.js
 *   ./gradlew perfLoad -Pvus=50 -Pduration=5m -PbaseUrl=http://localhost:8080
 */
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const VUS = Number(__ENV.VUS || 10);
const DURATION = __ENV.DURATION || '5m';

export const options = {
  vus: VUS,
  duration: DURATION,
  thresholds: {
    http_req_failed: ['rate<0.1'],
    http_req_duration: ['p(95)<3000'],
  },
};

export default function () {
  const supplier = http.get(`${BASE_URL}/api/v1/suppliers/SUP-001`);
  check(supplier, { 'supplier 200': (r) => r.status === 200 });

  const create = http.post(
    `${BASE_URL}/api/v1/products`,
    JSON.stringify({
      name: `Load Product ${__VU}-${__ITER}`,
      sku: `SKU-${__VU}-${__ITER}`,
      supplierId: 'SUP-001',
    }),
    { headers: { 'Content-Type': 'application/json' } }
  );
  check(create, { 'create 201': (r) => r.status === 201 });

  if (create.status === 201) {
    const id = create.json('id');
    const get = http.get(`${BASE_URL}/api/v1/products/${id}`);
    check(get, { 'get 200': (r) => r.status === 200 });
  }

  sleep(0.5);
}
