#!/bin/bash
BASE_URL="http://localhost:8080/api/orders"

echo "=== Семинар 3 — curl-тесты ==="
echo ""

echo "1. POST — отправить заказ ORD-001"
curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-001","product":"Laptop","quantity":1,"price":1299.99}' | jq .
echo ""

echo "2. POST — отправить заказ ORD-002"
curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-002","product":"Mouse","quantity":2,"price":49.99}' | jq .
echo ""

echo "3. POST — отправить заказ ORD-003"
curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-003","product":"Keyboard","quantity":1,"price":89.99}' | jq .
echo ""

echo "4. GET /stats — статистика обработанных заказов"
sleep 1
curl -s "$BASE_URL/stats" | jq .
echo ""

echo "=== Готово. Проверьте Kafka UI: http://localhost:8090 ==="
