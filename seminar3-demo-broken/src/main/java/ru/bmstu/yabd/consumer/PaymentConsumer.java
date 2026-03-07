package ru.bmstu.yabd.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.bmstu.yabd.model.OrderEvent;

// TODO 7: Добавьте аннотацию @Service на класс.
//   Без неё Spring не зарегистрирует класс как бин — @KafkaListener не заработает.

@Service
@Slf4j
public class PaymentConsumer {

    // TODO 8: Добавьте аннотацию @KafkaListener на метод listen:
    //   - topics = "orders"
    //   - groupId = "payment-processor"  ← другая группа, чем у OrderConsumer!
    //
    //   Внутри: залогируйте "Payment processed for: {orderId}"
    //   Подсказка: log.info("Payment processed for: {}", event.orderId())
    //
    //   Почему groupId другой? Две независимые группы читают один топик "orders"
    //   — это паттерн fan-out: каждое событие обрабатывается дважды параллельно.

    @KafkaListener(topics = "orders", groupId = "payment-processor")
    public void listen(OrderEvent event) {
        log.info("Payment processed for: {}", event.orderId());
    }
}
