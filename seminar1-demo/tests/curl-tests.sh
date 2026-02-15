#!/usr/bin/env bash
# =============================================
# curl-тесты для seminar1-demo
# Запуск: bash tests/curl-tests.sh
# Предварительно: docker compose up --build -d
# =============================================

set -euo pipefail

BASE_URL="http://localhost:8080"
PASS=0
FAIL=0

green() { printf "\033[32m%s\033[0m\n" "$1"; }
red()   { printf "\033[31m%s\033[0m\n" "$1"; }
bold()  { printf "\033[1m%s\033[0m\n" "$1"; }

assert_status() {
    local desc="$1" method="$2" url="$3" expected="$4"
    shift 4
    local status
    status=$(curl -s -o /dev/null -w "%{http_code}" -X "$method" "$url" "$@")
    if [ "$status" = "$expected" ]; then
        green "  PASS: $desc (HTTP $status)"
        PASS=$((PASS + 1))
    else
        red "  FAIL: $desc (expected $expected, got $status)"
        FAIL=$((FAIL + 1))
    fi
}

assert_json_field() {
    local desc="$1" url="$2" field="$3" expected="$4"
    local value
    value=$(curl -s "$url" | python3 -c "import sys,json; print(json.load(sys.stdin).get('$field',''))" 2>/dev/null || echo "ERROR")
    if [ "$value" = "$expected" ]; then
        green "  PASS: $desc ($field=$value)"
        PASS=$((PASS + 1))
    else
        red "  FAIL: $desc (expected $field=$expected, got $value)"
        FAIL=$((FAIL + 1))
    fi
}

# Wait for app to be ready
bold "Waiting for app to start..."
for i in $(seq 1 30); do
    if curl -s "$BASE_URL/api/orders/health" > /dev/null 2>&1; then
        green "App is ready!"
        break
    fi
    if [ "$i" -eq 30 ]; then
        red "App did not start in time"
        exit 1
    fi
    sleep 2
done

echo ""
bold "=== Test 1: Health Check ==="
assert_status "GET /api/orders/health" GET "$BASE_URL/api/orders/health" 200
assert_json_field "Health status is UP" "$BASE_URL/api/orders/health" "status" "UP"

echo ""
bold "=== Test 2: Send Order #1 ==="
assert_status "POST order ORD-001" POST "$BASE_URL/api/orders" 200 \
    -H "Content-Type: application/json" \
    -d '{"orderId":"ORD-001","product":"Laptop","quantity":1,"price":1299.99}'

echo ""
bold "=== Test 3: Send Order #2 ==="
assert_status "POST order ORD-002" POST "$BASE_URL/api/orders" 200 \
    -H "Content-Type: application/json" \
    -d '{"orderId":"ORD-002","product":"Phone","quantity":2,"price":799.50}'

echo ""
bold "=== Test 4: Send Order #3 ==="
assert_status "POST order ORD-003" POST "$BASE_URL/api/orders" 200 \
    -H "Content-Type: application/json" \
    -d '{"orderId":"ORD-003","product":"Tablet","quantity":3,"price":499.00}'

echo ""
bold "=== Test 5: Wait for consumer processing ==="
sleep 3

echo ""
bold "=== Test 6: Check Stats ==="
assert_status "GET /api/orders/stats" GET "$BASE_URL/api/orders/stats" 200

PROCESSED=$(curl -s "$BASE_URL/api/orders/stats" | python3 -c "import sys,json; print(json.load(sys.stdin).get('processedOrders',0))" 2>/dev/null || echo 0)
if [ "$PROCESSED" -ge 3 ]; then
    green "  PASS: Consumer processed $PROCESSED orders (expected >= 3)"
    PASS=$((PASS + 1))
else
    red "  FAIL: Consumer processed $PROCESSED orders (expected >= 3)"
    FAIL=$((FAIL + 1))
fi

echo ""
bold "=== Test 7: Send batch of orders ==="
for i in $(seq 4 8); do
    curl -s -o /dev/null -X POST "$BASE_URL/api/orders" \
        -H "Content-Type: application/json" \
        -d "{\"orderId\":\"ORD-00$i\",\"product\":\"Item-$i\",\"quantity\":$i,\"price\":$((i * 100)).00}"
done
green "  Sent 5 more orders (ORD-004 to ORD-008)"
PASS=$((PASS + 1))

sleep 3

PROCESSED2=$(curl -s "$BASE_URL/api/orders/stats" | python3 -c "import sys,json; print(json.load(sys.stdin).get('processedOrders',0))" 2>/dev/null || echo 0)
if [ "$PROCESSED2" -ge 8 ]; then
    green "  PASS: Total processed = $PROCESSED2 (expected >= 8)"
    PASS=$((PASS + 1))
else
    red "  FAIL: Total processed = $PROCESSED2 (expected >= 8)"
    FAIL=$((FAIL + 1))
fi

echo ""
bold "=============================="
bold "Results: $PASS passed, $FAIL failed"
bold "=============================="

if [ "$FAIL" -gt 0 ]; then
    exit 1
fi
