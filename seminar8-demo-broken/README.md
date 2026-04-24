# Семинар 8: KSQL vs SparkSQL — сравнение на практике

## Задание

Решите одну задачу (выручка по продуктам) двумя способами: через KSQL и SparkSQL.
Исправьте проект, чтобы Spring Boot отправлял заказы в Kafka, KSQL агрегировал данные
в реальном времени, а PySpark анализировал те же данные через SparkSQL.

## Список TODO (15 заданий)

### Java-код (Spring Boot producer)

| # | Файл | Задание |
|---|------|---------|
| 1 | `model/OrderEvent.java` | Замените класс на Java Record |
| 2 | `config/KafkaConfig.java` | Создайте бин `NewTopic` для топика `orders` |
| 3 | `producer/OrderProducer.java` | Реализуйте отправку события через `KafkaTemplate` |
| 4 | `controller/OrderController.java` | Добавьте `@RestController` и `@RequestMapping` |
| 5 | `controller/OrderController.java` | Создайте POST-метод для приёма заказов |

### Docker и сеть

| # | Файл | Задание |
|---|------|---------|
| 6 | `docker-compose.yml` | Исправьте Kafka listeners (dual: `kafka:29092` + `localhost:9092`) |
| 7 | `docker-compose.yml` | `KSQL_BOOTSTRAP_SERVERS` → `kafka:29092` |
| 8 | `docker-compose.yml` | `KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS` → `kafka:29092` |
| 9 | `docker-compose.yml` | `SPRING_KAFKA_BOOTSTRAP_SERVERS` → `kafka:29092` |

### KSQL

| # | Файл | Задание |
|---|------|---------|
| 10 | `ksql/init.sql` | Создайте STREAM `orders` (JSON) |
| 11 | `ksql/init.sql` | Создайте TABLE `product_revenue` с агрегацией |
| 12 | `ksql/init.sql` | Создайте TABLE `hourly_revenue` с Tumbling Window |

### SparkSQL (PySpark)

| # | Файл | Задание |
|---|------|---------|
| 13 | `spark/analysis.py` | Прочитайте данные из Kafka в batch-режиме |
| 14 | `spark/analysis.py` | Напишите SparkSQL-запрос (выручка по продуктам) |
| 15 | `spark/analysis.py` | Напишите тот же запрос через DataFrame API |

## Запуск

```bash
docker compose up -d
```

## Тестирование

```bash
chmod +x tests/curl-tests.sh
./tests/curl-tests.sh
```

## ksqlDB CLI

```bash
docker compose exec ksqldb-server ksql http://localhost:8088
```

## Spark submit

```bash
docker compose exec spark spark-submit \
  --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.5.0 \
  /spark/analysis.py
```
