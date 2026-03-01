# seminar2-demo-broken

Сломанный Spring Boot проект для семинара 2. Каркас приложения собран — ваша задача дописать недостающий код, чтобы CRUD-сервис заказов заработал.

## Что внутри

```
src/main/java/ru/bmstu/yabd/
├── Application.java            ✅ готов
├── model/
│   └── Order.java              ❌ пустой — нужно объявить record
├── service/
│   └── OrderService.java       ❌ методы кидают UnsupportedOperationException
└── controller/
    └── OrderController.java    ❌ нет аннотаций на классе, методы не реализованы
```

Конфигурация (`build.gradle`, `application.yml`, профили dev/prod, Dockerfile) — полностью рабочая. Проект компилируется, но падает при любом HTTP-запросе.

## Как запустить

```bash
./gradlew bootRun
```

Проверка после починки:

```bash
# создать заказ
curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"product":"Laptop","quantity":1,"price":1299.99}'

# список заказов
curl -s http://localhost:8080/api/orders

# статистика
curl -s http://localhost:8080/api/orders/stats
```

---

## Задания

### TODO 1 — Order record

**Файл:** `model/Order.java`

Объявите Java record с четырьмя полями:

| Поле | Тип |
|------|-----|
| id | String |
| product | String |
| quantity | int |
| price | double |

```java
public record Order(String id, String product, int quantity, double price) {}
```

---

### TODO 2 — OrderService.create()

**Файл:** `service/OrderService.java`

Реализуйте создание заказа:
1. Сгенерируйте `id` через `UUID.randomUUID().toString()`
2. Создайте новый `Order` с этим `id` и данными из входного объекта
3. Сохраните в `store`
4. Верните сохранённый объект

---

### TODO 3 — OrderService.findById()

**Файл:** `service/OrderService.java`

Верните заказ из `store` по ключу `id`.

---

### TODO 4 — OrderService.findAll()

**Файл:** `service/OrderService.java`

Верните все значения из `store`.

---

### TODO 5 — OrderService.update()

**Файл:** `service/OrderService.java`

Реализуйте обновление:
1. Создайте новый `Order` с переданным `id` и полями из входного объекта
2. Замените запись в `store`
3. Верните обновлённый объект

---

### TODO 6 — OrderService.delete()

**Файл:** `service/OrderService.java`

Удалите запись из `store` по ключу `id`.

---

### TODO 7 — OrderService.stats()

**Файл:** `service/OrderService.java`

Посчитайте статистику:
1. Общая сумма = `price * quantity` для каждого заказа (используйте Stream API)
2. Верните `Map` с ключами `"totalOrders"` и `"totalSum"`

---

### TODO 8 — Аннотации контроллера

**Файл:** `controller/OrderController.java`

Добавьте три аннотации на класс `OrderController`:

| Аннотация | Зачем |
|-----------|-------|
| `@RestController` | Делает класс REST-контроллером с автоматической JSON-сериализацией |
| `@RequestMapping("/api/orders")` | Базовый путь для всех эндпоинтов |
| `@RequiredArgsConstructor` | Lombok генерирует конструктор для `final`-полей (DI) |

---

### TODO 9 — POST /api/orders

**Файл:** `controller/OrderController.java`

1. Добавьте `@PostMapping`
2. Вызовите `service.create(order)`
3. Верните `ResponseEntity` со статусом **201** и телом

---

### TODO 10 — GET /api/orders

**Файл:** `controller/OrderController.java`

1. Добавьте `@GetMapping`
2. Верните `service.findAll()`

---

### TODO 11 — GET /api/orders/{id}

**Файл:** `controller/OrderController.java`

1. Добавьте `@GetMapping("/{id}")`
2. Верните `service.findById(id)`

---

### TODO 12 — PUT /api/orders/{id}

**Файл:** `controller/OrderController.java`

1. Добавьте `@PutMapping("/{id}")`
2. Верните `service.update(id, order)`

---

### TODO 13 — DELETE /api/orders/{id}

**Файл:** `controller/OrderController.java`

1. Добавьте `@DeleteMapping("/{id}")`
2. Вызовите `service.delete(id)`
3. Верните `ResponseEntity.noContent().build()` (статус **204**)

---

### TODO 14 — GET /api/orders/stats

**Файл:** `controller/OrderController.java`

1. Добавьте `@GetMapping("/stats")`
2. Верните `service.stats()`

---

## Чеклист самопроверки

После выполнения всех TODO убедитесь:

- [ ] `./gradlew bootRun` запускается без ошибок
- [ ] POST создаёт заказ и возвращает JSON с `id`
- [ ] GET `/api/orders` возвращает список всех заказов
- [ ] GET `/api/orders/{id}` возвращает конкретный заказ
- [ ] PUT обновляет заказ
- [ ] DELETE удаляет заказ, ответ — 204
- [ ] GET `/api/orders/stats` показывает количество и сумму

## Эталонное решение

Готовый рабочий проект: [`../seminar2-demo`](../seminar2-demo)
