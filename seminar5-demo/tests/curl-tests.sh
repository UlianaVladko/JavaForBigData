#!/bin/bash
echo "=== Семинар 5 — curl-тесты (Kafka Streams) ==="
echo ""
echo "1. POST — Laptop"
curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-001","product":"Laptop","quantity":1,"price":1299.99}' | python3 -m json.tool
echo ""
echo "2. POST — Mouse"
curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-002","product":"Mouse","quantity":2,"price":49.99}' | python3 -m json.tool
echo ""
echo "3. POST — Laptop (second)"
curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-003","product":"Laptop","quantity":1,"price":1499.99}' | python3 -m json.tool
echo ""
echo "4. POST — Keyboard"
curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-004","product":"Keyboard","quantity":1,"price":89.99}' | python3 -m json.tool
echo ""
echo "5. POST — Mouse (second)"
curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-005","product":"Mouse","quantity":3,"price":49.99}' | python3 -m json.tool
echo ""
echo "6. POST — Laptop (third)"
curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-006","product":"Laptop","quantity":1,"price":999.99}' | python3 -m json.tool
echo ""
echo "=== Готово. Проверьте топик product-counts в Kafka UI: http://localhost:8090 ==="
echo "=== Ожидаемый результат: Laptop=3, Mouse=2, Keyboard=1 ==="
