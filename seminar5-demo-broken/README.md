# seminar5-demo-broken

Сломанный Spring Boot + Kafka Streams проект для семинара 5. Инфраструктура готова — ваша задача дописать код потоковой аналитики: настроить Kafka Streams pipeline с `filter`, `groupBy`, `count`, чтобы pipeline `REST API → Kafka → Streams → product-counts` заработал.

## Что внутри

```
src/main/java/ru/bmstu/yabd/
├── Application.java              ❌ нет @EnableKafkaStreams
├── model/
│   └── OrderEvent.java           ❌ обычный класс вместо record
├── config/
│   ├── KafkaConfig.java          ❌ нет @Bean-методов для топиков
│   └── StreamConfig.java         ❌ нет Kafka Streams pipeline
├── producer/
│   └── OrderProducer.java        ❌ метод send() не реализован
└── controller/
    └── OrderController.java      ❌ нет аннотаций класса, метод не реализован
```

Конфигурация (`build.gradle`, `application.yml`, `docker-compose.yml`, `Dockerfile`) — полностью рабочая.

## Как запустить

```bash
# Запустить всё через Docker Compose
docker compose up --build

# Или только Kafka (приложение локально)
docker compose up kafka kafka-ui
./gradlew bootRun
```

Проверка после починки:

```bash
bash tests/curl-tests.sh

# Или вручную:
curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-001","product":"Laptop","quantity":1,"price":1299.99}'
```

Kafka UI: http://localhost:8090

---

## Задания

### TODO 1 — @EnableKafkaStreams

**Файл:** `Application.java`

Добавьте аннотацию `@EnableKafkaStreams` на класс `Application`. Не забудьте импорт:
```java
import org.springframework.kafka.annotation.EnableKafkaStreams;
```

---

### TODO 2 — OrderEvent record

**Файл:** `model/OrderEvent.java`

Замените обычный класс на Java record с четырьмя полями:

| Поле | Тип |
|------|-----|
| orderId | String |
| product | String |
| quantity | int |
| price | double |

```java
public record OrderEvent(String orderId, String product, int quantity, double price) {}
```

---

### TODO 3 — KafkaConfig: топик orders

**Файл:** `config/KafkaConfig.java`

Добавьте `@Bean`-метод `ordersTopic()`, возвращающий `NewTopic`:
- name: `"orders"`
- partitions: `3`
- replicas: `1`

```java
@Bean
public NewTopic ordersTopic() {
    return TopicBuilder.name("orders").partitions(3).replicas(1).build();
}
```

---

### TODO 4 — KafkaConfig: топик product-counts

**Файл:** `config/KafkaConfig.java`

Аналогично TODO 3, добавьте `@Bean`-метод `productCountsTopic()` для топика `"product-counts"` (3 партиции, 1 реплика).

---

### TODO 5 — StreamConfig: объявление @Bean метода

**Файл:** `config/StreamConfig.java`

Создайте `@Bean`-метод `analyticsStream(StreamsBuilder builder)`, возвращающий `KStream<String, OrderEvent>`.

```java
@Bean
public KStream<String, OrderEvent> analyticsStream(StreamsBuilder builder) {
    // ...
}
```

---

### TODO 6 — StreamConfig: создание KStream

**Файл:** `config/StreamConfig.java`

Внутри метода создайте `KStream` из топика `"orders"` с правильными Serde:

```java
JsonSerde<OrderEvent> orderSerde = new JsonSerde<>(OrderEvent.class);
orderSerde.configure(Map.of(
    "spring.json.trusted.packages", "*",
    "spring.json.value.default.type", "ru.bmstu.yabd.model.OrderEvent"
), false);

KStream<String, OrderEvent> stream = builder.stream(
    "orders",
    Consumed.with(Serdes.String(), orderSerde)
);
```

---

### TODO 7 — StreamConfig: цепочка обработки

**Файл:** `config/StreamConfig.java`

Постройте цепочку обработки потока:

1. `filter` — отбросить null и заказы с ценой <= 0
2. `groupBy` — группировка по продукту (`order.product()`)
3. `windowedBy` — окно 5 минут (`TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5))`)
4. `count` — подсчёт в каждом окне
5. `toStream` → `map` → `to("product-counts")`

```java
stream
    .filter((key, order) -> order != null && order.price() > 0)
    .groupBy(
        (key, order) -> order.product(),
        Grouped.with(Serdes.String(), orderSerde)
    )
    .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5)))
    .count(Materialized.<String, Long, WindowStore<Bytes, byte[]>>as("product-window-counts")
        .withKeySerde(Serdes.String())
        .withValueSerde(Serdes.Long()))
    .toStream()
    .map((windowedKey, count) -> KeyValue.pair(windowedKey.key(), count.toString()))
    .to("product-counts", Produced.with(Serdes.String(), Serdes.String()));
```

---

### TODO 8 — StreamConfig: Materialized state store

**Файл:** `config/StreamConfig.java`

Убедитесь, что в вызове `.count()` используется `Materialized.as("product-window-counts")` — это имя state store, необходимое для Kafka Streams.

> Это часть TODO 7 — вынесено отдельно, чтобы обратить внимание на Materialized API.

---

### TODO 9 — OrderProducer.send()

**Файл:** `producer/OrderProducer.java`

Реализуйте отправку события в Kafka:
1. Залогируйте событие через `log.info`
2. Отправьте в топик `"orders"`, ключ — `event.orderId()`, значение — `event`

```java
log.info("Sending to Kafka topic 'orders': {}", event);
kafkaTemplate.send("orders", event.orderId(), event);
```

---

### TODO 10 — Аннотации OrderController

**Файл:** `controller/OrderController.java`

Добавьте аннотации на класс:

| Аннотация | Зачем |
|-----------|-------|
| `@RestController` | JSON-ответы автоматически |
| `@RequestMapping("/api/orders")` | Базовый путь |

---

### TODO 11 — POST /api/orders

**Файл:** `controller/OrderController.java`

Создайте метод:

1. Добавьте `@PostMapping`
2. Параметр: `@RequestBody OrderEvent event`
3. Вызовите `producer.send(event)`
4. Верните `Map.of("orderId", event.orderId(), "status", "sent")`

```java
@PostMapping
public Map<String, String> create(@RequestBody OrderEvent event) {
    producer.send(event);
    return Map.of("orderId", event.orderId(), "status", "sent");
}
```

---

## Чеклист самопроверки

- [ ] `./gradlew compileJava` компилируется без ошибок (и до, и после починки)
- [ ] `docker compose up --build` запускается без ошибок
- [ ] POST `/api/orders` возвращает `{"orderId":"ORD-001","status":"sent"}`
- [ ] В Kafka UI (http://localhost:8090) виден топик `orders` с сообщениями
- [ ] В Kafka UI виден топик `product-counts` с агрегированными данными
- [ ] После отправки 6 заказов: Laptop=3, Mouse=2, Keyboard=1
- [ ] Все 11 TODO выполнены

## Эталонное решение

Готовый рабочий проект: [`../seminar5-demo`](../seminar5-demo)
