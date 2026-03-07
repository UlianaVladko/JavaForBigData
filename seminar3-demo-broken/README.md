# seminar3-demo-broken

Сломанный Spring Boot + Kafka проект для семинара 3. Инфраструктура готова — ваша задача дописать код Producer, Consumer и Controller, чтобы pipeline `REST API → Kafka → Consumer` заработал.

## Что внутри

```
src/main/java/ru/bmstu/yabd/
├── Application.java              ✅ готов
├── model/
│   └── OrderEvent.java           ❌ пустой — нужно объявить record
├── config/
│   └── KafkaConfig.java          ❌ нет @Bean-методов для топиков
├── producer/
│   └── OrderProducer.java        ❌ метод send() не реализован
├── consumer/
│   ├── OrderConsumer.java        ❌ нет счётчика и @KafkaListener
│   └── PaymentConsumer.java      ❌ нет аннотаций и реализации
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
  -d '{"orderId":"ORD-001","product":"Laptop","quantity":1,"price":1299.99}'

curl -s http://localhost:8080/api/orders/stats
```

Kafka UI: http://localhost:8090

---

## Задания

### TODO 1 — OrderEvent record

**Файл:** `model/OrderEvent.java`

Объявите Java record с четырьмя полями:

| Поле | Тип |
|------|-----|
| orderId | String |
| product | String |
| quantity | int |
| price | double |

---

### TODO 2 — KafkaConfig: топик orders

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

### TODO 3 — KafkaConfig: топик payments

**Файл:** `config/KafkaConfig.java`

Аналогично TODO 2, добавьте `@Bean`-метод `paymentsTopic()` для топика `"payments"` (3 партиции, 1 реплика).

---

### TODO 4 — OrderProducer.send()

**Файл:** `producer/OrderProducer.java`

Реализуйте отправку события в Kafka:
1. Залогируйте событие через `log.info`
2. Отправьте в топик `"orders"`, ключ — `event.orderId()`, значение — `event`

---

### TODO 5 — OrderConsumer: счётчик

**Файл:** `consumer/OrderConsumer.java`

Объявите поле:
```java
@Getter
private final AtomicInteger processedCount = new AtomicInteger(0);
```

---

### TODO 6 — OrderConsumer: @KafkaListener

**Файл:** `consumer/OrderConsumer.java`

Добавьте на метод `listen` аннотацию `@KafkaListener`:
- `topics = "orders"`
- `groupId = "order-processor"`

Внутри метода:
1. Залогируйте полученное событие
2. Увеличьте счётчик: `processedCount.incrementAndGet()`

---

### TODO 7 — PaymentConsumer: аннотации класса

**Файл:** `consumer/PaymentConsumer.java`

Добавьте на класс аннотации `@Service` и `@Slf4j`.

---

### TODO 8 — PaymentConsumer: @KafkaListener

**Файл:** `consumer/PaymentConsumer.java`

Добавьте на метод `listen` аннотацию `@KafkaListener`:
- `topics = "orders"`
- `groupId = "payment-processor"` ← **другая группа**, чем у OrderConsumer!

Внутри метода залогируйте:
```java
log.info("Payment processed for: {}", event.orderId());
```

> Обратите внимание: обе группы (`order-processor` и `payment-processor`) читают один топик `"orders"` независимо — это паттерн fan-out.

---

### TODO 9 — Аннотации OrderController

**Файл:** `controller/OrderController.java`

Добавьте три аннотации на класс:

| Аннотация | Зачем |
|-----------|-------|
| `@RestController` | JSON-ответы автоматически |
| `@RequestMapping("/api/orders")` | Базовый путь |
| `@RequiredArgsConstructor` | Constructor injection для final-полей |

---

### TODO 10 — POST /api/orders

**Файл:** `controller/OrderController.java`

1. Добавьте `@PostMapping`
2. Вызовите `producer.send(event)`
3. Верните `ResponseEntity.ok(Map.of("status", "sent", "orderId", event.orderId()))`

---

### TODO 11 — GET /api/orders/stats

**Файл:** `controller/OrderController.java`

1. Добавьте `@GetMapping("/stats")`
2. Верните `ResponseEntity.ok(Map.of("processedOrders", consumer.getProcessedCount().get()))`

---

## Чеклист самопроверки

- [ ] `docker compose up --build` запускается без ошибок
- [ ] POST `/api/orders` возвращает `{"status":"sent","orderId":"ORD-001"}`
- [ ] В логах видно `Order received: OrderEvent[...]` (OrderConsumer)
- [ ] В логах видно `Payment processed for: ORD-001` (PaymentConsumer)
- [ ] GET `/api/orders/stats` возвращает корректный счётчик
- [ ] В Kafka UI (http://localhost:8090) виден топик `orders` с сообщениями
- [ ] В Kafka UI видны две consumer groups: `order-processor` и `payment-processor`

## Эталонное решение

Готовый рабочий проект: [`../seminar3-demo`](../seminar3-demo)
