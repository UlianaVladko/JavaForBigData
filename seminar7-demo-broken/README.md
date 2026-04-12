# Семинар 7: KSQL на практике

## Задание

Исправьте проект, чтобы он корректно работал с Apache Kafka и ksqlDB.
Spring Boot приложение отправляет заказы в Kafka, а ksqlDB обрабатывает их через SQL.

## Список TODO (12 заданий)

### Java-код

| # | Файл | Задание |
|---|------|---------|
| 1 | `model/OrderEvent.java` | Замените класс на Java Record с полями: orderId, product, quantity, price |
| 2 | `config/KafkaConfig.java` | Создайте бин `NewTopic` для топика `orders` (3 партиции) |
| 3 | `producer/OrderProducer.java` | Реализуйте отправку события в топик `orders` через `KafkaTemplate` |
| 4 | `controller/OrderController.java` | Добавьте аннотации `@RestController` и `@RequestMapping("/api/orders")` |
| 5 | `controller/OrderController.java` | Создайте POST-метод для приёма и отправки заказов |

### Docker и сеть

| # | Файл | Задание |
|---|------|---------|
| 6 | `docker-compose.yml` | Исправьте Kafka listeners: настройте два listener-а (PLAINTEXT для контейнеров на kafka:29092, EXTERNAL для хоста на localhost:9092) |
| 7 | `docker-compose.yml` | Измените `KSQL_BOOTSTRAP_SERVERS` на `kafka:29092` |
| 8 | `docker-compose.yml` | Измените `KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS` на `kafka:29092` |
| 9 | `docker-compose.yml` | Измените `SPRING_KAFKA_BOOTSTRAP_SERVERS` на `kafka:29092` |

### KSQL

| # | Файл | Задание |
|---|------|---------|
| 10 | `ksql/init.sql` | Создайте STREAM `orders` поверх топика `orders` (JSON) |
| 11 | `ksql/init.sql` | Создайте TABLE `product_stats` с агрегацией COUNT + SUM по продуктам |
| 12 | `ksql/init.sql` | Создайте TABLE `hourly_orders` с Tumbling Window (1 час) |

## Запуск

```bash
docker compose up -d
```

## Тестирование

```bash
chmod +x tests/curl-tests.sh
./tests/curl-tests.sh
```

## Инициализация ksqlDB

После запуска выполните SQL из `ksql/init.sql` через ksqlDB CLI:
```bash
docker compose exec ksqldb-server ksql http://localhost:8088
```
Затем вставьте SQL-команды из `ksql/init.sql`.
