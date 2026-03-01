#!/usr/bin/env bash
# =============================================
# curl-тесты для seminar2-demo
# Запуск: bash curl-tests.sh
# Предварительно: gradlew.bat bootRun
# =============================================

set -uo pipefail  # не используем -e, чтобы скрипт не падал на пустых командах

BASE_URL="http://localhost:8080/api/orders"
PASS=0
FAIL=0

timestamp() { date +"[%H:%M:%S]"; }
green() { printf "\033[32m%s %s\033[0m\n" "$(timestamp)" "$1"; }
red()   { printf "\033[31m%s %s\033[0m\n" "$(timestamp)" "$1"; }
bold()  { printf "\033[1m%s\033[0m\n" "$1"; }

assert_status() {
    local desc="$1" method="$2" url="$3" expected="$4"
    shift 4
    local status
    status=$(curl -s -o /dev/null -w "%{http_code}" -X "$method" "$url" "$@" || echo "000")
    if [ "$status" = "$expected" ]; then
        green "PASS: $desc (HTTP $status)"
        PASS=$((PASS + 1))
    else
        red "FAIL: $desc (expected $expected, got $status)"
        FAIL=$((FAIL + 1))
    fi
}

extract_id() {
    echo "$1" | grep -o '"id":"[^"]*"' | sed 's/"id":"\([^"]*\)"/\1/' | tr -d '\r\n' || echo ""
}

count_items() {
    echo "$1" | grep -o '{' | wc -l | tr -d '\r\n' || echo 0
}


bold "Waiting for app to start..."
for i in $(seq 1 30); do
    if curl -s "$BASE_URL" > /dev/null 2>&1; then
        green "App is ready!"
        break
    fi
    if [ "$i" -eq 30 ]; then
        red "App did not start in time"
        exit 1
    fi
    sleep 2
done


bold "=== Test 1: GET empty list ==="
RESPONSE=$(curl -s "$BASE_URL")
assert_status "GET /api/orders" GET "$BASE_URL" 200

ITEMS=$(count_items "$RESPONSE")
ITEMS=$(echo "$ITEMS" | tr -d '\r\n ')
if [ "$ITEMS" -eq 0 ]; then
    green "Order list is empty"
    PASS=$((PASS + 1))
else
    red "Expected empty list, got $ITEMS items"
    FAIL=$((FAIL + 1))
fi


bold "=== Test 2: Create Orders ==="
RESPONSE=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{"product":"Phone","quantity":2,"price":1000}')
ID1=$(extract_id "$RESPONSE")
if [ -n "$ID1" ]; then
    green "Created order #1 with ID $ID1"
    PASS=$((PASS + 1))
else
    red "Order #1 not created"
    FAIL=$((FAIL + 1))
fi

RESPONSE=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{"product":"Laptop","quantity":1,"price":2500}')
ID2=$(extract_id "$RESPONSE")
if [ -n "$ID2" ]; then
    green "Created order #2 with ID $ID2"
    PASS=$((PASS + 1))
else
    red "Order #2 not created"
    FAIL=$((FAIL + 1))
fi


bold "=== Test 3: Check Stats ==="
RESPONSE=$(curl -s "$BASE_URL/stats")
assert_status "GET /api/orders/stats" GET "$BASE_URL/stats" 200

TOTAL=$(echo "$RESPONSE" | grep -o '"totalOrders":[0-9]*' | sed 's/.*://' | tr -d '\r\n ')
if [ "$TOTAL" -eq 2 ]; then
    green "totalOrders = 2"
    PASS=$((PASS + 1))
else
    red "totalOrders expected 2, got $TOTAL"
    FAIL=$((FAIL + 1))
fi


bold "=== Test 4: Update Order ==="
assert_status "PUT update order #1" PUT "$BASE_URL/$ID1" 200 \
  -H "Content-Type: application/json" \
  -d '{"product":"Phone Pro","quantity":3,"price":1200}'


bold "=== Test 5: Delete Order ==="
assert_status "DELETE order #2" DELETE "$BASE_URL/$ID2" 204


bold "=== Test 6: Stats after delete ==="
RESPONSE=$(curl -s "$BASE_URL/stats")
TOTAL2=$(echo "$RESPONSE" | grep -o '"totalOrders":[0-9]*' | sed 's/.*://' | tr -d '\r\n ')
if [ "$TOTAL2" -eq 1 ]; then
    green "totalOrders after delete = 1"
    PASS=$((PASS + 1))
else
    red "totalOrders expected 1, got $TOTAL2"
    FAIL=$((FAIL + 1))
fi


bold "=============================="
bold "Results: $PASS passed, $FAIL failed"
bold "=============================="

if [ "$FAIL" -gt 0 ]; then
    exit 1
fi