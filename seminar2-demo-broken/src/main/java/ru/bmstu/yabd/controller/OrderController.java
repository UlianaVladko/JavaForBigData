package ru.bmstu.yabd.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bmstu.yabd.model.Order;
import ru.bmstu.yabd.service.OrderService;

import java.util.Collection;
import java.util.Map;

// TODO 8: Добавьте аннотации на класс:
//   - @RestController — делает класс REST-контроллером (JSON-ответы автоматически)
//   - @RequestMapping("/api/orders") — базовый путь для всех эндпоинтов
//   - @RequiredArgsConstructor — Lombok генерирует конструктор для final-полей (DI)

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor

public class OrderController {

    private final OrderService service;

    // TODO 9: Добавьте аннотацию @PostMapping и реализуйте метод create:
    //   - Принимает Order из тела запроса (аннотация @RequestBody)
    //   - Вызывает service.create(order)
    //   - Возвращает ResponseEntity со статусом 201 и созданным заказом в теле
    //   Подсказка: return ResponseEntity.status(201).body(service.create(order));

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order order) {
        return ResponseEntity
                .status(201)
                .body(service.create(order));
//        throw new UnsupportedOperationException("TODO 9: implement create endpoint");
    }

    // TODO 10: Добавьте аннотацию @GetMapping и реализуйте метод list:
    //   - Возвращает все заказы через service.findAll()

    @GetMapping
    public Collection<Order> list() {
        return service.findAll();
//        throw new UnsupportedOperationException("TODO 10: implement list endpoint");
    }

    // TODO 11: Добавьте аннотацию @GetMapping("/{id}") и реализуйте метод get:
    //   - Принимает id из URL (аннотация @PathVariable)
    //   - Возвращает заказ через service.findById(id)

    @GetMapping("/{id}")
    public Order get(@PathVariable String id) {
        return service.findById(id);
//        throw new UnsupportedOperationException("TODO 11: implement get endpoint");
    }

    // TODO 12: Добавьте аннотацию @PutMapping("/{id}") и реализуйте метод update:
    //   - Принимает id из URL (@PathVariable) и Order из тела (@RequestBody)
    //   - Возвращает обновлённый заказ через service.update(id, order)

    @PutMapping("/{id}")
    public Order update(@PathVariable String id, @RequestBody Order order) {
        return service.update(id, order);
//        throw new UnsupportedOperationException("TODO 12: implement update endpoint");
    }

    // TODO 13: Добавьте аннотацию @DeleteMapping("/{id}") и реализуйте метод delete:
    //   - Принимает id из URL (@PathVariable)
    //   - Вызывает service.delete(id)
    //   - Возвращает ResponseEntity без тела со статусом 204
    //   Подсказка: return ResponseEntity.noContent().build();

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
//        throw new UnsupportedOperationException("TODO 13: implement delete endpoint");
    }

    // TODO 14: Добавьте аннотацию @GetMapping("/stats") и реализуйте метод stats:
    //   - Возвращает Map со статистикой через service.stats()

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return service.stats();
//        throw new UnsupportedOperationException("TODO 14: implement stats endpoint");
    }
}
