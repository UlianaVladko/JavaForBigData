package ru.bmstu.yabd.service;

import org.springframework.stereotype.Service;
import ru.bmstu.yabd.model.Order;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

    private final Map<String, Order> store = new ConcurrentHashMap<>();

    public Order create(Order order) {
        // TODO 2: Реализуйте создание заказа:
        //   1. Сгенерируйте уникальный id через UUID.randomUUID().toString()
        //   2. Создайте новый Order с этим id и полями из входного order
        //      (используйте order.product(), order.quantity(), order.price())
        //   3. Положите его в store по ключу id
        //   4. Верните сохранённый объект

        String id = UUID.randomUUID().toString();

        Order newOrder = new Order(
                id,
                order.product(),
                order.quantity(),
                order.price()
        );
        store.put(id, newOrder);
        return newOrder;
//        throw new UnsupportedOperationException("TODO 2: implement create");
    }

    public Order findById(String id) {
        // TODO 3: Верните заказ из store по id

        return store.get(id);
//        throw new UnsupportedOperationException("TODO 3: implement findById");
    }

    public Collection<Order> findAll() {
        // TODO 4: Верните все заказы из store
        // Подсказка: store.values()

        return store.values();
//        throw new UnsupportedOperationException("TODO 4: implement findAll");
    }

    public Order update(String id, Order order) {
        // TODO 5: Реализуйте обновление заказа:
        //   1. Создайте новый Order с переданным id и полями из order
        //   2. Замените объект в store
        //   3. Верните обновлённый объект

        Order updated = new Order(
                id,
                order.product(),
                order.quantity(),
                order.price()
        );

        store.put(id, updated);
        return updated;
//        throw new UnsupportedOperationException("TODO 5: implement update");
    }

    public void delete(String id) {
        // TODO 6: Удалите заказ из store по id

        store.remove(id);
//        throw new UnsupportedOperationException("TODO 6: implement delete");
    }

    public Map<String, Object> stats() {
        // TODO 7: Реализуйте статистику:
        //   1. Посчитайте общую сумму всех заказов (price * quantity для каждого)
        //      Подсказка: store.values().stream().mapToDouble(o -> o.price() * o.quantity()).sum()
        //   2. Верните Map с ключами "totalOrders" (количество) и "totalSum" (сумма)
        //      Подсказка: Map.of("totalOrders", ..., "totalSum", ...)

        int totalOrders = store.size();

        double totalSum = store.values().stream()
                .mapToDouble(o -> o.price() * o.quantity())
                .sum();

        return Map.of(
                "totalOrders", totalOrders,
                "totalSum", totalSum
        );
//        throw new UnsupportedOperationException("TODO 7: implement stats");
    }
}
