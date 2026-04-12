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
  kafka_topic = 'orders',
  value_format = 'JSON',
  timestamp = 'createdAt'
);

-- TODO: Processing stream (нормализация)
CREATE STREAM orders_enriched AS
SELECT
  orderId,
  product,
  quantity,
  price,
  totalAmount,
  createdAt
FROM orders_raw
EMIT CHANGES;

-- TODO: Бизнес-фильтр
CREATE STREAM high_value_orders AS
SELECT *
FROM orders_enriched
WHERE totalAmount > 1000
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
FROM orders_enriched
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
FROM orders_enriched
WINDOW TUMBLING (SIZE 1 HOUR, GRACE PERIOD 10 MINUTES)
GROUP BY product
EMIT CHANGES;