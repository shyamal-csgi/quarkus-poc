import http from 'k6/http';
import { check, sleep } from 'k6';

/**
 * Smoke: 1 VU, short duration. Hits health + critical endpoints.
 *
 *   BASE_URL=http://localhost:8080 k6 run performance-test/k6-smoke-test.js
 *   ./gradlew perfSmoke -PbaseUrl=http://localhost:8080
 */
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  vus: 1,
  duration: '30s',
  thresholds: {
    http_req_failed: ['rate<0.05'],
    http_req_duration: ['p(95)<2000'],
  },
};

export default function () {
  const health = http.get(`${BASE_URL}/q/health`);
  check(health, { 'health is 200': (r) => r.status === 200 });

  const suppliers = http.get(`${BASE_URL}/api/v1/suppliers`);
  check(suppliers, { 'suppliers list is 200': (r) => r.status === 200 });

  const supplier = http.get(`${BASE_URL}/api/v1/suppliers/SUP-001`);
  check(supplier, { 'supplier by id is 200': (r) => r.status === 200 });

  const products = http.get(`${BASE_URL}/api/v1/products`);
  check(products, { 'products list is 200': (r) => r.status === 200 });

  sleep(1);
}
