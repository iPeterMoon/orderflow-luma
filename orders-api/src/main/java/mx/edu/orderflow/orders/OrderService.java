package mx.edu.orderflow.orders;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Servicio de negocio encargado de la gestión y almacenamiento en memoria
 * de las órdenes.
 * <p>
 * Contiene métodos sincronizados para garantizar la consistencia.
 */
@Service
public class OrderService {
    private final Map<Long, Order> orders = new LinkedHashMap<>();
    private final AtomicLong ids = new AtomicLong(1000);

    public synchronized Order create(String customerId, BigDecimal total) {
        if (customerId == null || customerId.isBlank())
            throw new IllegalArgumentException("customerId required");
        if (total == null || total.signum() <= 0)
            throw new IllegalArgumentException("total must be positive");
        long id = ids.incrementAndGet();
        Order o = new Order(id, customerId, total, OrderStatus.CREATED);
        orders.put(id, o);
        return o;
    }

    public synchronized List<Order> list() {
        return new ArrayList<>(orders.values());
    }

    public synchronized Optional<Order> find(long id) {
        return Optional.ofNullable(orders.get(id));
    }

    public void unusedMethodWithBug(String param) {
        param.toLowerCase();
        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }
    }
}
