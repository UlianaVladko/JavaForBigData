-- ===== Семинар 8: KSQL-решение =====

-- TODO 10: Создайте STREAM orders поверх топика 'orders'
-- Поля: orderId VARCHAR KEY, product VARCHAR, quantity INT, price DOUBLE
-- Формат: JSON
CREATE STREAM orders (
    orderId VARCHAR,
    product VARCHAR,
    quantity INT,
    price DOUBLE,
    timestamp BIGINT
) WITH (
    KAFKA_TOPIC = 'orders',
    VALUE_FORMAT = 'JSON',
    TIMESTAMP = 'timestamp'
);

-- TODO 11: Создайте TABLE product_revenue с агрегацией
-- SELECT product, COUNT(*), SUM(price * quantity), AVG(price), MIN(price), MAX(price)
-- FROM orders GROUP BY product
CREATE TABLE product_revenue AS
SELECT
    product,
    COUNT(*) AS order_count,
    SUM(price * quantity) AS total_revenue,
    AVG(price) AS avg_price,
    MIN(price) AS min_price,
    MAX(price) AS max_price
FROM orders
GROUP BY product
EMIT CHANGES;

-- TODO 12: Создайте TABLE hourly_revenue с Tumbling Window (1 час)
-- SELECT product, COUNT(*), SUM(price * quantity) FROM orders
-- WINDOW TUMBLING (SIZE 1 HOUR) GROUP BY product
CREATE TABLE hourly_revenue AS
SELECT
    product,
    COUNT(*) AS order_count,
    SUM(price * quantity) AS total_revenue
FROM orders
WINDOW TUMBLING (SIZE 1 HOUR)
GROUP BY product
EMIT CHANGES;

SET 'auto.offset.reset' = 'earliest';