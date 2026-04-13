-- ===== Seminar 7: KSQL на практике =====

-- TODO 10: Создайте STREAM orders поверх топика 'orders'
-- Поля: orderId VARCHAR KEY, product VARCHAR, quantity INT, price DOUBLE
-- Формат: JSON
-- Подсказка:
-- CREATE STREAM orders (...) WITH (kafka_topic = 'orders', value_format = 'JSON');
CREATE STREAM orders_raw (
  orderId VARCHAR KEY,
  product VARCHAR,
  quantity INT,
  price DOUBLE,
  totalAmount DOUBLE,
  createdAt BIGINT
) WITH (
  kafka_topic = 'orders.v1',
  value_format = 'JSON',
  timestamp = 'createdAt'
);

CREATE STREAM orders_v2 (
  orderId VARCHAR,
  product VARCHAR,
  quantity INT,
  price DOUBLE,
  totalAmount DOUBLE,
  createdAt BIGINT,
  version INT,
  discountPrice DOUBLE
) WITH (
  kafka_topic = 'orders.v2',
  value_format = 'JSON'
);

-- TODO: Filtered stream
CREATE STREAM orders_clean AS
SELECT
  orderId,
  product,
  quantity,
  price,
  totalAmount,
  createdAt
FROM orders_raw
EMIT CHANGES;

CREATE STREAM orders_enriched_v2 AS
SELECT
  orderId,
  product,
  quantity,
  price,
  totalAmount,
  totalAmount * 0.9 AS discounted_total,
  createdAt
FROM orders_clean
EMIT CHANGES;

-- TODO: Business stream (real-time filter)
CREATE STREAM high_value_orders AS
SELECT *
FROM orders_clean
WHERE totalAmount > 6000
EMIT CHANGES;

-- TODO 11: Создайте TABLE product_stats с агрегацией по продуктам
-- Поля: product, COUNT(*) AS order_count, SUM(price * quantity) AS total_revenue
-- Подсказка:
-- CREATE TABLE product_stats AS SELECT ... FROM orders GROUP BY product EMIT CHANGES;
CREATE TABLE product_stats AS
SELECT
  product,
  COUNT(*) AS order_count,
  SUM(quantity) AS total_items,
  SUM(totalAmount) AS revenue,
  AVG(totalAmount) AS avg_order_value
FROM orders_clean
GROUP BY product
EMIT CHANGES;

-- TODO 12: Создайте TABLE hourly_orders с оконной агрегацией (Tumbling Window 1 час)
-- Поля: product, COUNT(*) AS cnt, SUM(price * quantity) AS revenue
-- Подсказка:
-- CREATE TABLE hourly_orders AS SELECT ... WINDOW TUMBLING (SIZE 1 HOUR) ...;
CREATE TABLE hourly_orders AS
SELECT
  product,
  COUNT(*) AS order_count,
  SUM(totalAmount) AS revenue
FROM orders_clean
WINDOW TUMBLING (SIZE 1 HOUR, GRACE PERIOD 10 MINUTES)
GROUP BY product
EMIT CHANGES;