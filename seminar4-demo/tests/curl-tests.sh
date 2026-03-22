#!/bin/bash
echo "=== Семинар 4 — curl-тесты ==="
echo ""
echo "1. POST — заказ из Moscow"
curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-001","product":"Laptop","quantity":1,"price":1299.99,"region":"Moscow"}' | python -m json.tool
echo ""
echo "2. POST — заказ из SPB"
curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-002","product":"Mouse","quantity":2,"price":49.99,"region":"SPB"}' | python -m json.tool
echo ""
echo "3. POST — заказ из Moscow"
curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-003","product":"Keyboard","quantity":1,"price":89.99,"region":"Moscow"}' | python -m json.tool
echo ""
echo "4. POST — заказ из Kazan"
curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-004","product":"Monitor","quantity":1,"price":599.99,"region":"Kazan"}' | python -m json.tool
echo ""
echo "5. GET /stats"
curl -s http://localhost:8080/api/orders/stats | python -m json.tool
echo ""
echo "6. Duplicate ORD-001 (exactly-once check)"
curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-001","product":"Laptop","quantity":1,"price":1299.99,"region":"Moscow"}' | python -m json.tool
echo ""
echo "7. GET /stats"
curl -s http://localhost:8080/api/orders/stats | python -m json.tool
echo "=== Готово. Проверьте Kafka UI: http://localhost:8090 ==="
echo "=== Заказы из Moscow должны быть в одной партиции ==="
