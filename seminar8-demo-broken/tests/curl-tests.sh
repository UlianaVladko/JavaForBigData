#!/bin/bash
echo "=== Семинар 8: KSQL vs SparkSQL ==="
echo ""
echo "--- 1. Отправляем заказы через REST API ---"

curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"O-001","product":"Laptop","quantity":2,"price":75000.0}' | jq .

curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"O-002","product":"Mouse","quantity":5,"price":1500.0}' | jq .

curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"O-003","product":"Laptop","quantity":1,"price":95000.0}' | jq .

curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"O-004","product":"Keyboard","quantity":3,"price":3500.0}' | jq .

curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"O-005","product":"Mouse","quantity":10,"price":1200.0}' | jq .

curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"O-006","product":"Monitor","quantity":1,"price":45000.0}' | jq .

echo ""
echo "--- Ждём 3 секунды ---"
sleep 3

echo ""
echo "--- 2. KSQL: Pull-запрос ---"
curl -s -X POST http://localhost:8088/query \
  -H "Content-Type: application/vnd.ksql.v1+json" \
  -d '{"ksql":"SELECT * FROM product_revenue;","streamsProperties":{}}' | jq .

echo ""
echo "--- 3. SparkSQL: Batch-анализ ---"
echo "Запустите вручную:"
echo "  docker compose exec spark spark-submit \\"
echo "    --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.5.0 \\"
echo "    /spark/analysis.py"

echo ""
echo "=== Тесты завершены ==="
echo "  Spring Boot API:  http://localhost:8080"
echo "  ksqlDB REST:      http://localhost:8088"
echo "  Spark UI:         http://localhost:8081"
echo "  Kafka UI:         http://localhost:8090"
