#!/bin/bash

echo "Sending order 1"
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"TEST-1","product":"Test","region":"Moscow","quantity":1,"price":10}'

echo "Sending duplicate order 1"
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"TEST-1","product":"Test","region":"Moscow","quantity":1,"price":10}'

echo "Sending order 2"
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"TEST-2","product":"Test","region":"Moscow","quantity":1,"price":10}'