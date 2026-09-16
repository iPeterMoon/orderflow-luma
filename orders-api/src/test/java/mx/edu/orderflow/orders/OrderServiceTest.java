package mx.edu.orderflow.orders;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderServiceTest {
    @Test
    void createsValidOrder() {
        var s = new OrderService();
        var o = s.create("student-1", new BigDecimal("150.00"));
        assertEquals(OrderStatus.CREATED, o.status());
        assertEquals("student-1", o.customerId());
    }

    @Test
    void rejectsNegativeTotal() {
        var s = new OrderService();
        var negativeTotal = new BigDecimal("-1");
        assertThrows(IllegalArgumentException.class, () -> s.create("student-1", negativeTotal));
    }
}
