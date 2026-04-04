#!/bin/bash
echo "=== Семинар 6 — curl-тесты (Interactive Queries + Join) ==="
echo ""

echo "--- Шаг 1: Добавляем клиентов ---"
echo ""
echo "1. POST customer C-001"
curl -s -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"customerId":"C-001","name":"Иван Петров","email":"ivan@example.com"}' | python3 -m json.tool
echo ""
echo "2. POST customer C-002"
curl -s -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"customerId":"C-002","name":"Мария Сидорова","email":"maria@example.com"}' | python3 -m json.tool
echo ""

sleep 3
echo "--- Шаг 2: Отправляем заказы ---"
echo ""
echo "3. POST order ORD-001 (Laptop, customer C-001)"
curl -s -X POST "http://localhost:8080/api/orders?customerId=C-001" \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-001","product":"Laptop","quantity":1,"price":1299.99}' | python3 -m json.tool
echo ""
echo "4. POST order ORD-002 (Mouse, customer C-002)"
curl -s -X POST "http://localhost:8080/api/orders?customerId=C-002" \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-002","product":"Mouse","quantity":2,"price":49.99}' | python3 -m json.tool
echo ""
echo "5. POST order ORD-003 (Laptop, customer C-001)"
curl -s -X POST "http://localhost:8080/api/orders?customerId=C-001" \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-003","product":"Laptop","quantity":1,"price":1499.99}' | python3 -m json.tool
echo ""
echo "6. POST order ORD-004 (Keyboard, customer C-002)"
curl -s -X POST "http://localhost:8080/api/orders?customerId=C-002" \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-004","product":"Keyboard","quantity":1,"price":89.99}' | python3 -m json.tool
echo ""

sleep 5
echo "--- Шаг 3: Проверяем статистику (Interactive Queries) ---"
echo ""
echo "7. GET /api/stats/Laptop"
curl -s http://localhost:8080/api/stats/Laptop | python3 -m json.tool
echo ""
echo "8. GET /api/stats/Mouse"
curl -s http://localhost:8080/api/stats/Mouse | python3 -m json.tool
echo ""
echo "9. GET /api/stats (все продукты)"
curl -s http://localhost:8080/api/stats | python3 -m json.tool
echo ""
echo "=== Готово. Проверьте топик enriched-orders в Kafka UI: http://localhost:8090 ==="
echo "=== Ожидаемый результат: Laptop=2, Mouse=1, Keyboard=1 ==="
echo "=== enriched-orders должен содержать заказы с именами клиентов ==="
