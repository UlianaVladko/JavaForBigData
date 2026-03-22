# seminar4-demo-broken

Сломанный Spring Boot + Kafka проект для семинара 4. Инфраструктура готова — ваша задача дописать код для сериализации, кастомного партиционирования и обработки ошибок (DLT), чтобы pipeline `REST API → Kafka (partitioned) → Consumer → DLT` заработал.

## Что внутри

```
src/main/java/ru/bmstu/yabd/
├── Application.java              ✅ готов
├── model/
│   └── OrderEvent.java           ❌ класс вместо record — нужно заменить на record
├── partitioner/
│   └── RegionPartitioner.java    ❌ метод partition() не реализован
├── config/
│   ├── KafkaConfig.java          ❌ нет @Bean-методов для топиков
│   └── ErrorHandlerConfig.java   ❌ нет @Bean для error handler с DLT
├── producer/
│   └── OrderProducer.java        ❌ метод send() не реализован
├── consumer/
│   ├── OrderConsumer.java        ❌ нет счётчика и @KafkaListener
│   └── DltConsumer.java          ❌ нет счётчика и @KafkaListener
└── controller/
    └── OrderController.java      ❌ нет аннотаций класса, методы не реализованы
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
  -d '{"orderId":"ORD-001","product":"Laptop","quantity":1,"price":1299.99,"region":"Moscow"}'

curl -s http://localhost:8080/api/orders/stats
```

Kafka UI: http://localhost:8090

---

## Задания

### TODO 1 — OrderEvent record

**Файл:** `model/OrderEvent.java`

Замените класс на Java record с пятью полями:

| Поле | Тип |
|------|-----|
| orderId | String |
| product | String |
| quantity | int |
| price | double |
| region | String |

---

### TODO 2 — RegionPartitioner: partition()

**Файл:** `partitioner/RegionPartitioner.java`

Реализуйте метод `partition()`:
1. Получите количество партиций: `cluster.partitionCountForTopic(topic)`
2. Если `key == null`, верните `0`
3. Иначе верните `Math.abs(key.toString().hashCode()) % numPartitions`

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

### TODO 4 — KafkaConfig: топик orders.DLT

**Файл:** `config/KafkaConfig.java`

Аналогично TODO 3, добавьте `@Bean`-метод `dltTopic()` для топика `"orders.DLT"` (1 партиция, 1 реплика).

---

### TODO 5 — ErrorHandlerConfig: error handler с DLT

**Файл:** `config/ErrorHandlerConfig.java`

Добавьте `@Bean`-метод `errorHandler(KafkaOperations<String, Object> kafkaOps)`:
1. Создайте `DeadLetterPublishingRecoverer` с `kafkaOps`
2. Создайте `FixedBackOff(1000L, 3)` — 3 повтора с интервалом 1 секунда
3. Верните `new DefaultErrorHandler(recoverer, backoff)`

---

### TODO 6 — OrderProducer.send()

**Файл:** `producer/OrderProducer.java`

Реализуйте отправку события в Kafka:
1. Залогируйте событие через `log.info`
2. Отправьте в топик `"orders"`, ключ — `event.region()`, значение — `event`
3. Обработайте результат через `thenAccept` — залогируйте партицию и offset

> Обратите внимание: ключ — это **регион**, а не `orderId`. Это обеспечивает, что заказы из одного региона попадают в одну партицию.

---

### TODO 7 — OrderConsumer: счётчик

**Файл:** `consumer/OrderConsumer.java`

Объявите поле:
```java
@Getter
private final AtomicInteger processedCount = new AtomicInteger(0);
```

---

### TODO 8 — OrderConsumer: @KafkaListener

**Файл:** `consumer/OrderConsumer.java`

Создайте метод `listen(OrderEvent event)` с аннотацией `@KafkaListener`:
- `topics = "orders"`
- `groupId = "order-processor"`

Внутри метода:
1. Залогируйте полученное событие
2. Увеличьте счётчик: `processedCount.incrementAndGet()`

---

### TODO 9 — DltConsumer: счётчик

**Файл:** `consumer/DltConsumer.java`

Объявите поле:
```java
@Getter
private final AtomicInteger dltCount = new AtomicInteger(0);
```

---

### TODO 10 — DltConsumer: @KafkaListener

**Файл:** `consumer/DltConsumer.java`

Создайте метод `listen(Object record)` с аннотацией `@KafkaListener`:
- `topics = "orders.DLT"`
- `groupId = "dlt-processor"`

Внутри метода:
1. Залогируйте DLT-сообщение через `log.warn`
2. Увеличьте счётчик: `dltCount.incrementAndGet()`

---

### TODO 11 — Аннотации OrderController

**Файл:** `controller/OrderController.java`

Добавьте аннотации на класс:

| Аннотация | Зачем |
|-----------|-------|
| `@RestController` | JSON-ответы автоматически |
| `@RequestMapping("/api/orders")` | Базовый путь |

---

### TODO 12 — POST /api/orders

**Файл:** `controller/OrderController.java`

1. Добавьте `@PostMapping`
2. Вызовите `producer.send(event)`
3. Верните `ResponseEntity.ok(Map.of("orderId", event.orderId(), "region", event.region(), "status", "sent"))`

---

### TODO 13 — GET /api/orders/stats

**Файл:** `controller/OrderController.java`

1. Добавьте `@GetMapping("/stats")`
2. Верните `ResponseEntity.ok(Map.of("processedOrders", consumer.getProcessedCount().get(), "dltMessages", dltConsumer.getDltCount().get()))`

---

## Чеклист самопроверки

- [ ] `docker compose up --build` запускается без ошибок
- [ ] POST `/api/orders` возвращает `{"orderId":"ORD-001","region":"Moscow","status":"sent"}`
- [ ] В логах видно `Order received: ORD-001` (OrderConsumer)
- [ ] Заказы из одного региона (Moscow) попадают в одну партицию
- [ ] GET `/api/orders/stats` возвращает корректный счётчик
- [ ] В Kafka UI (http://localhost:8090) виден топик `orders` с 3 партициями
- [ ] В Kafka UI виден топик `orders.DLT`
- [ ] Consumer group `order-processor` видна в Kafka UI

## Эталонное решение

Готовый рабочий проект: [`../seminar4-demo`](../seminar4-demo)
